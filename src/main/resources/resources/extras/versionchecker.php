<?php
// Copyright 2012 Visual Illusions Entertainment.
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

    /*Gets the versions file*/
	$file = @file("versions.txt");
	
	/*Gets the requested name*/
    $requested = html_entity_decode($_GET['name']);
	$ver = "0";
    
    if(empty($file)) {
        echo $ver;
    }
    else {
        $names = array();
            foreach($file as $line) {
                $split = preg_split("/=/", $line);
                $names[$split[0]] = $split[1];
            }
            foreach($names as $name => $version) {
                if($name === $requestedPlugin) {
					$ver = $version;
					break;
				}
			}
    }
	echo $ver;
?>
