$nameprefix = "com.blueseer"

$dir = get-childitem c:\bsnb\blueseer\src\com\blueseer

foreach ($d in $dir) {
  $newdir = "c:\bsnb\blueseer\src\com\blueseer\" + $d
  $cdir = get-childitem $newdir
  foreach ($n in $cdir) {
    $ns = $n.tostring()
    if ($ns.endswith(".java")) {
      $word = $ns.substring(0,$ns.length-5)
      $name = $nameprefix + "." + $d + "." + $word
      write-host $word ',' $name
    }
  }
}


