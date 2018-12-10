$afile = gc xref
$bfile = gc mymenu

foreach ($b in $bfile) {
    $bar = $b.split(",")
    if ($bar[3] -eq "JMenu") {
     write-host $b
     continue
    }
   foreach ($x in $afile) {
      $xar = $x.split(",")
      if ($bar[2] -eq $xar[0]) {
       $bar[2] = $xar[1]
       $new = $bar -join ","
       write-host $new
      }
   }
}
