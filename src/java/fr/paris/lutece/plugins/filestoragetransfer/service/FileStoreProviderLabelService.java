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
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.filestoragetransfer.service;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.i18n.I18nService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * Gives the label of the file store service providers, read from the key filestore.&lt;provider name&gt;.label of the configuration: a text, or an
 * #i18n{key} localized in the language of the administrator. A provider without label is shown by its technical name.
 */
@ApplicationScoped
public class FileStoreProviderLabelService
{
    private static final String PREFIX_KEY = "filestore.";
    private static final String SUFFIX_KEY = ".label";

    @Inject
    @Any
    private Instance<IFileStoreServiceProvider> _fileStoreServiceProviders;

    /**
     * Returns the key of the label of a provider
     *
     * @param strProviderName
     *            the technical name of the provider
     * @return the key filestore.&lt;provider name&gt;.label
     */
    public static String getLabelKey( String strProviderName )
    {
        return PREFIX_KEY + strProviderName + SUFFIX_KEY;
    }

    /**
     * Returns the label of a provider
     *
     * @param strProviderName
     *            the technical name of the provider
     * @param locale
     *            the language of the label
     * @return the configured label, localized, or the technical name when none is configured
     */
    public String getLabel( String strProviderName, Locale locale )
    {
        if ( StringUtils.isBlank( strProviderName ) )
        {
            return Objects.toString( strProviderName, "" );
        }

        Config config = ConfigProvider.getConfig( );
        String strLabel = config.getOptionalValue( getLabelKey( strProviderName ), String.class ).orElse( null );

        if ( StringUtils.isBlank( strLabel ) )
        {
            return strProviderName;
        }

        String strLocalized = I18nService.localize( strLabel, locale );

        return StringUtils.isBlank( strLocalized ) ? strProviderName : strLocalized;
    }

    /**
     * Returns the providers of the site with their labels, sorted by label
     *
     * @param locale
     *            the language of the labels
     * @return the providers
     */
    public List<FileStoreProviderLabel> getProviders( Locale locale )
    {
        Collator collator = Collator.getInstance( locale );

        return _fileStoreServiceProviders.stream( ).map( IFileStoreServiceProvider::getName ).distinct( )
                .map( strName -> new FileStoreProviderLabel( strName, getLabel( strName, locale ) ) )
                .sorted( Comparator.comparing( FileStoreProviderLabel::getLabel, collator ) ).collect( Collectors.toList( ) );
    }

    /**
     * Returns the labels of the providers of the site and of some other provider names (those of stored requests, whose provider may be gone)
     *
     * @param listProviderNames
     *            the other provider names
     * @param locale
     *            the language of the labels
     * @return the labels by technical name
     */
    public Map<String, String> getLabels( Collection<String> listProviderNames, Locale locale )
    {
        Map<String, String> mapLabels = new LinkedHashMap<>( );
        getProviders( locale ).forEach( provider -> mapLabels.put( provider.getName( ), provider.getLabel( ) ) );
        listProviderNames.stream( ).filter( StringUtils::isNotBlank ).forEach( strName -> mapLabels.computeIfAbsent( strName, n -> getLabel( n, locale ) ) );

        return mapLabels;
    }
}
