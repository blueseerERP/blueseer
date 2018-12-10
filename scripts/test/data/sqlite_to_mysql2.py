import re, fileinput

def main():
  for line in fileinput.input():
    process = False
    for nope in ('BEGIN TRANSACTION','COMMIT',
                 'sqlite_sequence','CREATE UNIQUE INDEX'):
      if nope in line: break
    else:
      process = True
    if not process: continue
    m = re.search('CREATE TABLE "([a-z_]*)"(.*)', line)
    if m:
      name, sub = m.groups()
      sub = sub.replace('"','`')
      line = '''DROP TABLE IF EXISTS %(name)s;
CREATE TABLE IF NOT EXISTS %(name)s%(sub)s
'''
      line = line % dict(name=name, sub=sub)
    else:
      m = re.search('INSERT INTO "([a-z_]*)"(.*)', line)
      if m:
        line = 'INSERT INTO %s%s\n' % m.groups()
        line = line.replace('"', r'\"')
        line = line.replace('"', "'")
    line = re.sub(r"([^'])'t'(.)", "\\1THIS_IS_TRUE\\2", line)
    line = line.replace('THIS_IS_TRUE', '1')
    line = re.sub(r"([^'])'f'(.)", "\\1THIS_IS_FALSE\\2", line)
    line = line.replace('THIS_IS_FALSE', '0')
    line = line.replace('AUTOINCREMENT', 'AUTO_INCREMENT')
    if re.search('^CREATE INDEX', line):
        line = line.replace('"','`')
    print(line,)

main()
