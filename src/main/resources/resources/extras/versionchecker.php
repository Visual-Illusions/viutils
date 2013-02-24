<?php
/* versionchecker.php v1.1

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
  If not, see http://www.gnu.org/licenses/lgpl.html
*/

function main($program){
	/* Edit the programinfo array to match your programs
	 Examples:
	$stable = array( "VIUtils" => array( "VERSION" => "1.0","BUILD" => "1"),
	                 "Other"   => array( "VERSION" => "1.0","BUILD" => "1")
	);
	$relcan = array( "VIUtils" => array( "VERSION" => "1.0","BUILD" => "1")
	);
	$beta = array( "VIUtils" => array( "VERSION" => "1.0","BUILD" => "1")
	);
	$beta = array( "VIUtils" => array( "VERSION" => "1.0","BUILD" => "1")
	); 
	*/
	
	$stable	    = array(
	);
	$relcan     = array(
	);
	$beta       = array(
	);
	$alpha      = array(
	);
	
	$toret = '';
	if (array_key_exists($program,$stable)){ //Look up program
		$toret .= 'STABLE:';
		$toret .= 'VERSION='.$stable[$program]['VERSION'].':';
		$toret .= 'BUILD='.$stable[$program]['BUILD'];
	}
	
	if (array_key_exists($program,$relcan)){
		if(!empty($toret)){
			$toret .= ',';
		}
		$toret .= 'RELEASE_CANIDATE:';
		$toret .= 'VERSION='.$relcan[$program]['VERSION'].':';
		$toret .= 'BUILD='.$relcan[$program]['BUILD'];
	}
	
	if (array_key_exists($program,$beta)){
		if(!empty($toret)){
			$toret .= ',';
		}
		$toret .= 'BETA:';
		$toret .= 'VERSION='.$beta[$program]['VERSION'].':';
		$toret .= 'BUILD='.$beta[$program]['BUILD'];
	}
	
	if (array_key_exists($program,$alpha)){
		if(!empty($toret)){
			$toret .= ',';
		}
		$toret .= 'ALPHA:';
		$toret .= 'VERSION='.$alpha[$program]['VERSION'].':';
		$toret .= 'BUILD='.$alpha[$program]['BUILD'];
	}
	
	if(empty($toret)){
		echo 'ERROR: 404';
	}
	else{
		echo $toret;
	}
}

if(isset($_POST['program'])){
	main($_POST['program']);
}
else{
	echo 'ERROR: 400';
}
?>
