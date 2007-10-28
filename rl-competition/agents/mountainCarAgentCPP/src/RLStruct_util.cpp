/* Structure Copier
* Copyright (C) 2007, Leah Hackman
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */

#include "RLStruct_util.h"


void copyRLStruct(RL_abstract_type& oldStruct, RL_abstract_type newStruct)
{
//Copy the contents of an old RL_abstract_type to the new RL_abstract_type
	unsigned int i =0;
	oldStruct.numInts = newStruct.numInts;
	oldStruct.numDoubles = newStruct.numDoubles;
	for(i=0; i<newStruct.numInts; i++)
	oldStruct.intArray[i] = newStruct.intArray[i];
	for(i=0;i<newStruct.numDoubles;i++)
	oldStruct.doubleArray[i] = newStruct.doubleArray[i];
}
