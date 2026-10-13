Run with JDK 26 or newer and Python 3 with `paramiko` installed:

```sh
python3 -m venv /tmp/blueseer-comm-env
/tmp/blueseer-comm-env/bin/pip install paramiko
JAVA_HOME=/path/to/jdk /tmp/blueseer-comm-env/bin/python tests/communications/run.py
```

The runner compiles the application using `lib/*`, then calls `runFTPClient()`
and `postAS2()` against loopback SSH/SFTP and HTTP servers. It creates a temporary
SQLite database from the repository schema and generates disposable SSH keys,
certificates, MDNs and outbound files. It does not use the application database
or contact trading partners.

On platforms unsupported by the bundled SQLite native library, set
`SQLITE_JDBC_JAR` to a compatible sqlite-jdbc JAR (tested with 3.49.1.0 on macOS
ARM64). All fixtures and build outputs are removed when the runner exits.

Checks cover SSH key authentication, trusted/unknown/changed host keys, explicit
host-checking opt-out, valid/tampered/error signed MDNs, optional unsigned MDNs,
required-but-missing signatures, and debug-mode file retention.

For before/after testing, the host-key cases provide a disposable identity file
named `yes` after the private-key-path case. This isolates host verification from
the original key-path typo, which otherwise prevents the SSH handshake entirely.

SFTP now defaults to `StrictHostKeyChecking=yes`: configure `knownHostsPath`
with the expected server key. The explicit `StrictHostKeyChecking=no` override
is still supported for environments that deliberately disable this check.
