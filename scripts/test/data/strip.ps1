param(
[string] $inputfile,
[string] $outputfile
)


$writer = new-object IO.StreamWriter "$($PWD.Path)\$outputfile"
$writer.write([String]::join("`n",$(gc $inputfile)))
$writer.close()