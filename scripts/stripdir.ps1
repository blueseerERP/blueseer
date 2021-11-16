param(
[string] $dir
)

$files = get-childitem $dir
for ($i = 0; $i -lt $files.count; $i++) {
$outfile = $files[$i].name + ".zzz"
write-output $outfile
#$writer = new-object IO.StreamWriter "$($PWD.Path)\$outfile"
$writer = [IO.StreamWriter]::new("$($PWD.Path)\$outfile", $false, [System.Text.Utf8Encoding]::new())
$writer.write([String]::join("`n",$(gc -encoding UTF8 $files[$i])))
$writer.close()
rm $files[$i]
mv $outfile $files[$i]
}
