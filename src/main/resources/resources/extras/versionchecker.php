<?php
/* versionchecker.php v1.0

  Copyright 2012-2013 Visual Illusions Entertainment.
  
  This file is part of VIUtils.

  VIUtils is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  VIUtils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public License along with VIUtils.
  If not, see http://www.gnu.org/licenses/lgpl.html */

/* Edit the programinfo array to match your programs
Example:
$programinfo = array( "VIUtils" => array( "version" => "1.0","build" => "1","isBeta" => "true", "isRC" => "false")
                      );*/
$programinfo = array( //Program info array
                    );
  
if(isset($_POST['program'])){ //Check if post contians program, if not, drop the whole thing

    function died($error) { //prints an error message
        echo "ERROR: ".$error;
        die();
    }
    
    function progEcho($prog){ //prints false:<latest version info>
        global $programinfo;
        echo 'false:'.$programinfo[$prog]['version'].'b'.$programinfo[$prog]['build'].' ';
        if($programinfo[$prog]['isBeta'] == 'true'){
            echo 'BETA';
        }
        elseif($programinfo[$prog]['isRC'] == 'true'){
            echo 'RC';
        }
        die();
    }
    
    function checkBetaRC($prog){
        if($programinfo[$prog]['isBeta'] == 'true'){
            if($isBeta == 'true'){ //is requesting program a beta build?
                progEcho($prog); //current program status is upgrade over requesting program, send info
            }
            echo 'true'; // requesting program is upgrade or equal
        }
        else if($programinfo[$prog]['isRC'] == 'true'){
            if($isBeta == 'true'){ //is requesting program a beta build?
                progEcho($prog); //current program status is upgrade over requesting program, send info
            }
            else if($isRC == 'true'){
                progEcho($prog); //current program status is upgrade over requesting program, send info
            }
            echo 'true'; // requesting program is upgrade or equal
        }
        else{
            progEcho($prog); //current program status is upgrade over requesting program, send info
        }
        die();
    }
    

    if(!isset($_POST['version'])){ //if version not set, error and die
        died("Version not set");
    }
    elseif(!isset($_POST['build'])) { //if build not set, error and die
        died("Build not set");    
    }
    elseif(!isset($_POST['isBeta'])){ //if isBeta not set, error and die
        died("Beta not set");
    }
    elseif(!isset($_POST['isRC'])){ //if isRC not set, error and die
        died("RC not set");
    }

    $program = $_POST['program']; //The program to look for
    $version = $_POST['version']; //The version of the requesting program
    $build = $_POST['build'];     //The build of the requesting program
    $isBeta = $_POST['isBeta'];   //Wether the program is a beta or not
    $isRC = $_POST['isRC'];       //Wether the program is a release candidate or not
    $checkUnstable = false;       //Wether to include beta/rc in the checking

    if(isset($_POST['checkunstable'])){
        $checkUnstable = $_POST['checkunstable'];
    }
    
    if (array_key_exists($program,$programinfo)){ //Look up program
        if($programinfo[$program]['version'] > $version){ //Check version
            if($checkUnstable){ //Are we checking beta/rc ?
                checkBetaRC($program);
            }
            else if($programinfo[$prog]['isBeta'] == 'false' && $programinfo[$prog]['isRC'] == 'false'){
                progEcho($prog);
            }
        }
        else if($programinfo[$program]['build'] > $build){ //Check build
            if($checkUnstable){
               checkBetaRC($program);
            }
            else if($programinfo[$prog]['isBeta'] == 'false' && $programinfo[$prog]['isRC'] == 'false'){
                progEcho($prog);
            }
        }
        else{
            echo 'true'; // requesting program is upgrade or equal
        }
    }
    else{
        died("Program Not Found"); //program posted was not in the array
    }
}
else{
    echo "ERROR: No program set to check."; //program not posted
}
?>
