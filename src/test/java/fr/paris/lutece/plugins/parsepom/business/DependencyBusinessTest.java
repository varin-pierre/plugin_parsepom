/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.parsepom.business;

import fr.paris.lutece.test.LuteceTestCase;


public class DependencyBusinessTest extends LuteceTestCase
{
    private final static String GROUPID1 = "GroupId1";
    private final static String GROUPID2 = "GroupId2";
    private final static String ARTIFACTID1 = "ArtifactId1";
    private final static String ARTIFACTID2 = "ArtifactId2";
    private final static String VERSION1 = "Version1";
    private final static String VERSION2 = "Version2";
    private final static String TYPE1 = "Type1";
    private final static String TYPE2 = "Type2";
    private final static int SITEID1 = 1;
    private final static int SITEID2 = 2;

    public void testBusiness(  )
    {
        // Initialize an object
        Dependency dependency = new Dependency();
        dependency.setGroupId( GROUPID1 );
        dependency.setArtifactId( ARTIFACTID1 );
        dependency.setVersion( VERSION1 );
        dependency.setType( TYPE1 );
        dependency.setSiteId( SITEID1 );

        // Create test
        DependencyHome.create( dependency );
        Dependency dependencyStored = DependencyHome.findByPrimaryKey( dependency.getId( ) );
        assertEquals( dependencyStored.getGroupId() , dependency.getGroupId( ) );
        assertEquals( dependencyStored.getArtifactId() , dependency.getArtifactId( ) );
        assertEquals( dependencyStored.getVersion() , dependency.getVersion( ) );
        assertEquals( dependencyStored.getType() , dependency.getType( ) );
        assertEquals( dependencyStored.getSiteId() , dependency.getSiteId( ) );

        // Update test
        dependency.setGroupId( GROUPID2 );
        dependency.setArtifactId( ARTIFACTID2 );
        dependency.setVersion( VERSION2 );
        dependency.setType( TYPE2 );
        dependency.setSiteId( SITEID2 );
        DependencyHome.update( dependency );
        dependencyStored = DependencyHome.findByPrimaryKey( dependency.getId( ) );
        assertEquals( dependencyStored.getGroupId() , dependency.getGroupId( ) );
        assertEquals( dependencyStored.getArtifactId() , dependency.getArtifactId( ) );
        assertEquals( dependencyStored.getVersion() , dependency.getVersion( ) );
        assertEquals( dependencyStored.getType() , dependency.getType( ) );
        assertEquals( dependencyStored.getSiteId() , dependency.getSiteId( ) );

        // List test
        DependencyHome.getDependencysList();

        // Delete test
        DependencyHome.remove( dependency.getId( ) );
        dependencyStored = DependencyHome.findByPrimaryKey( dependency.getId( ) );
        assertNull( dependencyStored );
        
    }

}