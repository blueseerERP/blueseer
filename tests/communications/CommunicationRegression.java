import bsmf.MainFrame;
import com.blueseer.adm.admData;
import com.blueseer.edi.apiUtils;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

/** Runs the actual communication methods against disposable local fixtures. */
public class CommunicationRegression {
    private static int failures;
    private static KeyPair signingKey;
    private static X509Certificate certificate;

    public static void main(String[] args) throws Exception {
        Path repo = Path.of(args[0]);
        Security.addProvider(new BouncyCastleProvider());
        MainFrame.remoteDB = false;
        MainFrame.ds = null;
        MainFrame.url = "jdbc:sqlite:";
        MainFrame.db = Path.of("fixture.db").toAbsolutePath().toString();
        MainFrame.dbtype = "sqlite";
        MainFrame.driver = "org.sqlite.JDBC";
        MainFrame.user = "";
        MainFrame.pass = "";
        MainFrame.tags = ResourceBundle.getBundle("resources.bs", Locale.US);
        Class.forName(MainFrame.driver);
        Files.createDirectories(Path.of("edi/mdn"));
        Files.createDirectories(Path.of("out"));
        Files.createDirectories(Path.of("temp"));
        Files.createDirectories(Path.of("logs"));
        String schema = Files.readString(repo.resolve("sf/data/blueseer.sqlite"));
        for (String table : new String[]{"ftp_mstr", "ftp_attr", "edi_ctrl", "as2_mstr", "as2_log", "pks_mstr", "sys_meta", "code_mstr", "as2_meta"}) {
            Matcher matcher = Pattern.compile("CREATE TABLE \"" + table + "\" \\(.*?\\n\\);", Pattern.DOTALL).matcher(schema);
            if (!matcher.find()) throw new IllegalStateException("Missing schema: " + table);
            sql(matcher.group());
        }
        sql("INSERT INTO edi_ctrl (edic_indir, edic_outdir, edic_signkey, edic_as2url) VALUES ('out', 'out', 'signer', 'local')");
        generateCertificate();
        sql("INSERT INTO pks_mstr (pks_id, pks_type, pks_file) VALUES ('cert', 'publickey', 'certificate.der')");
        sql("INSERT INTO pks_mstr (pks_id, pks_type, pks_file, pks_parent) VALUES ('signer', 'keypair', 'signer.p12', 'store')");
        sql("INSERT INTO pks_mstr (pks_id, pks_type, pks_file) VALUES ('store', 'store', 'signer.p12')");
        sql("UPDATE pks_mstr SET pks_user='signer', pks_pass='" + MainFrame.PassWord("0", "fixture".toCharArray()) + "' WHERE pks_id='signer'");
        sql("UPDATE pks_mstr SET pks_storepass='" + MainFrame.PassWord("0", "fixture".toCharArray()) + "' WHERE pks_id='store'");

        HttpServer http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        try {
            http.start();
            sql("INSERT INTO as2_mstr (as2_id, as2_protocol, as2_url, as2_port, as2_outdir, as2_enccert, as2_signcert, as2_contenttype, as2_micalgo) VALUES ('partner', 'http', '127.0.0.1', '" + http.getAddress().getPort() + "', 'out', 'cert', 'cert', 'application/edi-x12', 'SHA-1')");
            as2Case(http, "valid signed MDN", true, false, false, false, false, true);
            as2Case(http, "tampered signed MDN", true, true, false, false, false, false);
            as2Case(http, "signed error MDN", true, false, true, false, false, false);
            as2Case(http, "optional unsigned MDN", false, false, false, false, false, true);
            as2Case(http, "required signature missing", false, false, false, true, false, false);
            as2Case(http, "debug preserves file", true, false, false, false, true, true);
        } finally {
            http.stop(0);
        }
        sql("INSERT INTO ftp_mstr (ftp_id, ftp_ip, ftp_port, ftp_login, ftp_sftp, ftp_commands) VALUES ('partner', '127.0.0.1', '" + args[1] + "', 'test', '1', 'ls')");
        sql("INSERT INTO ftp_attr VALUES ('partner', 'usePrivateKey', 'yes')");
        sql("INSERT INTO ftp_attr VALUES ('partner', 'privateKeyPath', '" + args[2] + "')");
        sql("INSERT INTO ftp_attr VALUES ('partner', 'PreferredAuthentications', 'publickey')");
        ftpCase("key authentication with trusted host", args[3], null, true);
        // Isolate host checking from the original key-path bug for before/after runs.
        Files.copy(Path.of(args[2]), Path.of("yes"));
        ftpCase("unknown host rejected by default", null, null, false);
        ftpCase("changed host rejected by default", args[4], null, false);
        ftpCase("explicit host checking opt-out", null, "no", true);
        if (failures != 0) throw new AssertionError(failures + " regression checks failed");
        System.out.println("All 10 communication regression checks passed");
    }

    private static void generateCertificate() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        signingKey = generator.generateKeyPair();
        X500Name name = new X500Name("CN=Local regression fixture");
        certificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(
            new JcaX509v3CertificateBuilder(name, BigInteger.ONE, new Date(0),
                new Date(System.currentTimeMillis() + 86400000L), name, signingKey.getPublic())
                .build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(signingKey.getPrivate())));
        Files.write(Path.of("certificate.der"), certificate.getEncoded());
        java.security.KeyStore store = java.security.KeyStore.getInstance("PKCS12");
        store.load(null, null);
        store.setKeyEntry("signer", signingKey.getPrivate(), "fixture".toCharArray(), new java.security.cert.Certificate[]{certificate});
        try (var out = Files.newOutputStream(Path.of("signer.p12"))) { store.store(out, "fixture".toCharArray()); }
    }

    private static void as2Case(HttpServer http, String label, boolean signed, boolean tampered,
            boolean error, boolean required, boolean debug, boolean success) throws Exception {
        sql("UPDATE as2_mstr SET as2_forcesigned='" + (required ? "1" : "0") + "'");
        http.createContext("/", exchange -> {
            try {
                exchange.getRequestBody().readAllBytes();
                String disposition = error ? "processed/error: authentication-failed" : "processed";
                MimeBodyPart notification = new MimeBodyPart();
                notification.setContent("Original-Message-ID: " + exchange.getRequestHeaders().getFirst("Message-ID")
                    + "\r\nDisposition: automatic-action/MDN-sent-automatically; " + disposition + "\r\n", "message/disposition-notification");
                MimeBodyPart text = new MimeBodyPart();
                text.setText("Local MDN fixture");
                MimeMultipart report = new MimeMultipart("report; report-type=disposition-notification");
                report.addBodyPart(text);
                report.addBodyPart(notification);
                MimeBodyPart reportPart = new MimeBodyPart();
                reportPart.setContent(report);
                reportPart.setHeader("Content-Type", report.getContentType());
                MimeMultipart response = report;
                if (signed) {
                    SMIMESignedGenerator signer = new SMIMESignedGenerator();
                    signer.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                        new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(
                        new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(signingKey.getPrivate()), certificate));
                    signer.addCertificates(new CollectionStore<>(java.util.List.of(new JcaX509CertificateHolder(certificate))));
                    response = signer.generate(reportPart);
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                response.writeTo(bytes);
                byte[] body = bytes.toByteArray();
                if (tampered) body = new String(body, StandardCharsets.ISO_8859_1)
                    .replace("; processed", "; processEd").getBytes(StandardCharsets.ISO_8859_1);
                exchange.getResponseHeaders().set("Content-Type", response.getContentType());
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                exchange.close();
            }
        });
        try {
            Path file = Path.of("out/document.edi");
            Files.writeString(file, "EDI regression payload");
            String result = apiUtils.postAS2("partner", debug);
            boolean retained = Files.exists(file);
            check(label, retained == (!success || debug) && result.contains("status__pass") == success,
                "file retained=" + retained + ", status__pass=" + result.contains("status__pass") + " response=" + result.substring(0, Math.min(120, result.length())));
            Files.deleteIfExists(file);
        } finally {
            http.removeContext("/");
        }
    }

    private static void ftpCase(String label, String knownHosts, String strict, boolean success) throws Exception {
        sql("DELETE FROM ftp_attr WHERE ftpa_key IN ('knownHostsPath', 'StrictHostKeyChecking')");
        if (knownHosts != null) sql("INSERT INTO ftp_attr VALUES ('partner', 'knownHostsPath', '" + knownHosts + "')");
        if (strict != null) sql("INSERT INTO ftp_attr VALUES ('partner', 'StrictHostKeyChecking', '" + strict + "')");
        ArrayList<String[]> result = admData.runFTPClient("partner");
        StringBuilder log = new StringBuilder();
        boolean listing = false;
        boolean error = false;
        for (String[] entry : result) {
            error |= "1".equals(entry[0]);
            listing |= entry[1].contains("fixture.txt");
            log.append(entry[1]).append('\n');
        }
        check(label, success ? listing && !error : !listing && error && log.toString().contains("HostKey"), log.toString());
    }

    private static void check(String label, boolean passed, String observed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + label + " -- " + observed);
        if (!passed) failures++;
    }

    private static void sql(String query) throws Exception {
        try (Connection connection = DriverManager.getConnection(MainFrame.url + MainFrame.db);
             Statement statement = connection.createStatement()) { statement.executeUpdate(query); }
    }
}
