/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.io;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.model.parameters.ConfigurationOptions;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

/**
 * A utility class which can be used by implementations to provide a toString rendering of OWL API
 * objects. The renderer can be set through the ConfigurtionOptions class, with property file or
 * system property. No local override is possible, because ToStringRenderer has no access to
 * ontology or ontology manager objects. Be careful of changing the value of the options in a
 * multithreaded application, as this will cause the renderer to change behaviour.
 *
 * For a more precise rendering use the syntax specific methods in OWLObject, where the desired
 * forma is specified as input.
 *
 * @author Matthew Horridge, The University Of Manchester, Bio-Health Informatics Group
 * @since 2.2.0
 */
public final class ToStringRenderer {
    static <Q, T> LoadingCache<Q, T> build(CacheLoader<Q, T> c) {
        return Caffeine.newBuilder().weakKeys().softValues().build(c);
    }

    static OWLObjectRenderer renderer(String className) {
        try {
            return (OWLObjectRenderer) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException("Custom renderer unavailable: " + className);
        }
    }

    static String value() {
        return ConfigurationOptions.TO_STRING_RENDERER.getValue(String.class, null);
    }

    private static final LoadingCache<String, OWLObjectRenderer> renderers =
        build(ToStringRenderer::renderer);

    /**
     * @return the singleton instance
     */
    public static OWLObjectRenderer getInstance() {
        return renderers.get(value());
    }

    /**
     * @param object the object to render
     * @return the rendering for the object
     */
    public static String getRendering(OWLObject object) {
        return getInstance().render(checkNotNull(object, "object cannot be null"));
    }

    public static OWLObjectRenderer getInstance(OWLDocumentFormat format, PrefixManager pm) {
        // TODO Auto-generated method stub
        return null;
    }

    public static OWLObjectRenderer getInstance(OWLDocumentFormat format) {
        return getInstance(format, null);
    }
}