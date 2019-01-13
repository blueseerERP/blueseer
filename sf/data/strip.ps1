param(
[string] $inputfile
)

$outfile = $inputfile + ".tev"
$writer = new-object IO.StreamWriter "$($PWD.Path)\$outfile"
$writer.write([String]::join("`n",$(gc $inputfile)))
$writer.close()
rm $inputfile
mv $outfile $inputfile
