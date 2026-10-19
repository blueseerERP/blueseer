#!/usr/bin/env python3
"""Loopback-only integration fixtures. Requires paramiko; uses disposable files."""
import os
from pathlib import Path
import socket
import subprocess
import tempfile
import threading
import time

import paramiko

REPO = Path(__file__).resolve().parents[2]
JAVA_HOME = os.environ.get("JAVA_HOME")
JAVA = str(Path(JAVA_HOME) / "bin/java") if JAVA_HOME else "java"
JAVAC = str(Path(JAVA_HOME) / "bin/javac") if JAVA_HOME else "javac"


class Auth(paramiko.ServerInterface):
    def __init__(self, allowed):
        self.allowed = allowed

    def get_allowed_auths(self, username):
        return "publickey"

    def check_auth_publickey(self, username, key):
        return paramiko.AUTH_SUCCESSFUL if username == "test" and key == self.allowed else paramiko.AUTH_FAILED

    def check_channel_request(self, kind, channel_id):
        return paramiko.OPEN_SUCCEEDED if kind == "session" else paramiko.OPEN_FAILED_ADMINISTRATIVELY_PROHIBITED


class Sftp(paramiko.SFTPServerInterface):
    def stat(self, path):
        entry = paramiko.SFTPAttributes()
        entry.st_mode = 0o40755
        entry.st_size = 0
        entry.st_uid = entry.st_gid = 0
        entry.st_atime = entry.st_mtime = 0
        return entry

    lstat = stat

    def list_folder(self, path):
        entry = paramiko.SFTPAttributes()
        entry.filename = "fixture.txt"
        entry.st_mode = 0o100644
        entry.st_size = 7
        entry.st_uid = entry.st_gid = 0
        entry.st_atime = entry.st_mtime = 0
        return [entry]


def main():
    with tempfile.TemporaryDirectory(prefix="bs-comm-") as temporary:
        work = Path(temporary)
        classes = work / "classes"
        classes.mkdir()
        sources = work / "sources.txt"
        sources.write_text("\n".join('"' + str(p) + '"' for p in REPO.glob("src/**/*.java"))
                           + '\n"' + str(REPO / "tests/communications/CommunicationRegression.java") + '"\n')
        subprocess.run([JAVAC, "-J-Xmx2g", "--release", "21", "-cp", str(REPO / "lib/*"), "-d", str(classes), "@" + str(sources)], check=True)
        host = paramiko.RSAKey.generate(2048)
        client = paramiko.RSAKey.generate(2048)
        identity = work / "identity.pem"
        client.write_private_key_file(str(identity))
        listener = socket.socket()
        listener.bind(("127.0.0.1", 0))
        listener.listen()
        listener.settimeout(0.2)
        port = listener.getsockname()[1]
        known = work / "known_hosts"
        known.write_text(f"[127.0.0.1]:{port} ssh-rsa {host.get_base64()}\n")
        wrong = work / "wrong_hosts"
        wrong.write_text(f"[127.0.0.1]:{port} ssh-rsa {paramiko.RSAKey.generate(2048).get_base64()}\n")
        stop = threading.Event()
        transports = []
        workers = []

        def serve_connection(connection):
            transport = paramiko.Transport(connection)
            transports.append(transport)
            try:
                transport.add_server_key(host)
                transport.set_subsystem_handler("sftp", paramiko.SFTPServer, Sftp)
                transport.start_server(server=Auth(client))
                while transport.is_active() and not stop.is_set():
                    time.sleep(0.05)
            except (EOFError, paramiko.SSHException):
                pass  # Expected when the client rejects an unknown/changed host key.
            finally:
                transport.close()

        def accept():
            while not stop.is_set():
                try:
                    connection, _ = listener.accept()
                except socket.timeout:
                    continue
                worker = threading.Thread(target=serve_connection, args=(connection,), daemon=True)
                workers.append(worker)
                worker.start()

        thread = threading.Thread(target=accept)
        thread.start()
        try:
            # Override bundled SQLite on platforms unsupported by its old native library.
            extra = os.environ.get("SQLITE_JDBC_JAR")
            classpath = os.pathsep.join(filter(None, [str(classes), str(REPO / "src"), extra, str(REPO / "lib/*")]))
            result = subprocess.run([JAVA, "-Djava.awt.headless=true", "-cp", classpath,
                "CommunicationRegression", str(REPO), str(port), str(identity), str(known), str(wrong)], cwd=work, timeout=120)
        finally:
            stop.set()
            thread.join(timeout=2)
            listener.close()
            for transport in transports:
                transport.close()
            for worker in workers:
                worker.join(timeout=2)
        raise SystemExit(result.returncode)


if __name__ == "__main__":
    main()
