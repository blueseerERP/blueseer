param(
[string] $myfile
)

gc $myfile -encoding byte | % { "{0:X2}" -f $_} | ? {$_ -eq "0D"} 
