/*
 * Copyright (c) 2002-2024, City of Paris
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
 * SUBSTITUTE GOODS OR SERVICES LOSS OF USE, DATA, OR PROFITS OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.filestoragetransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Test of the labels of the file store service providers
 */
public class FileStoreProviderLabelServiceTest extends LuteceTestCase
{
    private static final String DEFAULT_PROVIDER = "defaultDatabaseFileStoreProvider";

    @Inject
    private FileStoreProviderLabelService _labelService;

    /**
     * An #i18n label is localized, a text label is kept, a missing or blank label gives the technical name
     */
    @Test
    public void testGetLabel( )
    {
        assertEquals( "Base de données Lutece", _labelService.getLabel( DEFAULT_PROVIDER, Locale.FRENCH ), "i18n label in French" );
        assertEquals( "Lutece database", _labelService.getLabel( DEFAULT_PROVIDER, Locale.ENGLISH ), "i18n label in English" );
        assertEquals( "Libellé en texte", _labelService.getLabel( "unitTestTextProvider", Locale.FRENCH ), "text label" );
        assertEquals( "unitTestBlankProvider", _labelService.getLabel( "unitTestBlankProvider", Locale.FRENCH ), "blank label" );
        assertEquals( "noSuchProvider", _labelService.getLabel( "noSuchProvider", Locale.FRENCH ), "no label" );
        assertEquals( "", _labelService.getLabel( null, Locale.FRENCH ), "no name" );
    }

    /**
     * The providers of the site come with their labels, sorted by label, and the labels map also covers names of stored requests
     */
    @Test
    public void testProviders( )
    {
        List<FileStoreProviderLabel> listProviders = _labelService.getProviders( Locale.FRENCH );

        assertTrue( listProviders.stream( ).anyMatch( p -> DEFAULT_PROVIDER.equals( p.getName( ) ) && "Base de données Lutece".equals( p.getLabel( ) ) ),
                "default provider with its label" );
        for ( int i = 1; i < listProviders.size( ); i++ )
        {
            assertTrue( listProviders.get( i - 1 ).getLabel( ).compareToIgnoreCase( listProviders.get( i ).getLabel( ) ) <= 0, "sorted by label" );
        }

        Map<String, String> mapLabels = _labelService.getLabels( List.of( "unitTestTextProvider", "goneProvider" ), Locale.FRENCH );
        assertEquals( "Base de données Lutece", mapLabels.get( DEFAULT_PROVIDER ), "provider of the site" );
        assertEquals( "Libellé en texte", mapLabels.get( "unitTestTextProvider" ), "provider named by a request" );
        assertEquals( "goneProvider", mapLabels.get( "goneProvider" ), "provider gone" );
    }
}
