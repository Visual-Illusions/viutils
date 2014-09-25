<?php
/*
  programchecker.php v1.0
    
  Copyright (C) ${project.inceptionYear}-${current.year} Visual Illusions Entertainment.
    
  This file is part of VIUtils.

  VIUtils is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public License along with this library.
  If not, see http://www.gnu.org/licenses/lgpl.html
  
*/

/* Whether to enable browser-based query */
$get = false;

function main($program){
    /* Edit the programs array to match your programs
       Acceptable Statuses: STABLE, RELEASE CANDIDATE, SNAPSHOT, BETA, ALPHA
       Examples:
       $programs = array(
                    'VIUtils' => array( 'VERSION' => array('MAJOR' => '1', 'MINOR' => '3', 'REVISION' => '0'), 'STATUS' => 'SNAPSHOT'),
                    'VIBotX' => array( 'VERSION' => array('MAJOR' => '0', 'MINOR' => '1', 'REVISION' => '0'), 'STATUS' => 'SNAPSHOT')
                );
    */
    
    $programs = array(

                );
    
    if (array_key_exists($program,$programs)){ //Look up program
        echo json_encode($programs[$program]);
    }
    else {
        echo "ERROR: Program Not Found";
    }
}

if(isset($_POST['program'])) {
    main($_POST['program']);
}
else if ($get and isset($_GET['program'])) {
    main($_GET['program']);
}
else{
    header('HTTP/1.1 403 Forbidden');
}

?>
