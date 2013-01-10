<?php
// Copyright 2012-2013 Visual Illusions Entertainment.
//  
// This file is part of VIUtils.
//
// VIUtils is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// VIUtils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
// without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License along with VIUtils.
// If not, see http://www.gnu.org/licenses/lgpl.html

// Edit the programinfo array to match your programs
// Example:
//$programinfo = array( "VIUtils" => array( "version" => "1.0","build" => "1","isBeta" => "true", "isRC" => "false")
//					   );
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

    $program = $_POST['program'];
	$version = $_POST['version'];
	$build = $_POST['build'];
	$isBeta = false;
	$isRC = false;
	
	if($_POST['isBeta'] == 'true'){ //check isBeta
		$isBeta = true;
	}
	elseif($_POST['isRC'] == 'true'){ //check isRC
		$isRC = true;
	}
	
	if (array_key_exists($program,$programinfo)){ //Look up program
		if((int)$programinfo[$program]['version'] > (int)$version){ //Check version
			progEcho($program); //current program version greater than requesting program, send info
		}
		elseif((int)$programinfo[$program]['build'] > (int)$build){
			if($programinfo[$program]['isBeta'] == 'true'){
				if($isBeta == 'true'){ //is requesting program a beta build?
					progEcho($program); //current program status is upgrade over requesting program, send info
				}
				// requesting program is upgrade or equal
			}
			elseif($programinfo[$program]['isRC'] == 'true'){
				if($isBeta == 'true'){ //is requesting program a beta build?
					progEcho($program); //current program status is upgrade over requesting program, send info
				}
				elseif($isRC == 'true'){
					progEcho($program); //current program status is upgrade over requesting program, send info
				}
				// requesting program is upgrade or equal
			}
		}
	}
	else{
		died("Program Not Found"); //program posted was not in the array
	}
	echo 'true'; // requesting program is upgrade or equal
}
else{
	echo "ERROR: No program set to check."; //program not posted
}
?>
