/*
 * #%L
 * ACS AEM Commons Bundle
 * %%
 * Copyright (C) 2013 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.adobe.acs.commons.genericlists.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.adobe.acs.commons.genericlists.GenericList;
import com.adobe.acs.commons.genericlists.GenericList.Item;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;

@RunWith(MockitoJUnitRunner.class)
public class GenericListAdapterFactoryTest {
    @Mock
    private Page listPage;

    @Mock
    private Resource contentResource;

    @Mock
    private Resource listResource;

    @Mock
    private Resource resourceOne;

    @Mock
    private Resource resourceTwo;

    private AdapterFactory adapterFactory;

    @Before
    public void setup() {
        when(listPage.getProperties()).thenAnswer(new Answer<ValueMap>() {
            @SuppressWarnings("serial")
            public ValueMap answer(InvocationOnMock invocation) throws Throwable {
                return new ValueMapDecorator(new HashMap<String, Object>() {
                    {
                        put(NameConstants.NN_TEMPLATE, GenericListImpl.TMPL_GENERIC_LIST);

                    }
                });
            }
        });
        when(listPage.getContentResource()).thenReturn(contentResource);
        when(contentResource.getChild("list")).thenReturn(listResource);
        when(listResource.listChildren()).thenReturn(Arrays.asList(resourceOne, resourceTwo).iterator());

        when(resourceOne.adaptTo(ValueMap.class)).thenAnswer(new Answer<ValueMap>() {
            @SuppressWarnings("serial")
            public ValueMap answer(InvocationOnMock invocation) throws Throwable {
                return new ValueMapDecorator(new HashMap<String, Object>() {
                    {
                        put(NameConstants.PN_TITLE, "titleone");
                        put(GenericListImpl.PN_VALUE, "valueone");

                    }
                });
            }
        });
        when(resourceTwo.adaptTo(ValueMap.class)).thenAnswer(new Answer<ValueMap>() {
            @SuppressWarnings("serial")
            public ValueMap answer(InvocationOnMock invocation) throws Throwable {
                return new ValueMapDecorator(new HashMap<String, Object>() {
                    {
                        put(NameConstants.PN_TITLE, "titletwo");
                        put(GenericListImpl.PN_VALUE, "valuetwo");

                    }
                });
            }
        });

        adapterFactory = new GenericListAdapterFactory();
    }

    @Test
    public void test_that_adapting_page_with_correct_template_returns_directly() {
        GenericList list = adapterFactory.getAdapter(listPage, GenericList.class);
        assertNotNull(list);
        List<Item> items = list.getItems();
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("titleone", items.get(0).getTitle());
        assertEquals("valueone", items.get(0).getValue());
    }

    @Test
    public void test_that_adapting_page_with_wrong_template_returns_null() {
        Page wrongPage = mock(Page.class);

        when(wrongPage.getProperties()).thenAnswer(new Answer<ValueMap>() {
            @SuppressWarnings("serial")
            public ValueMap answer(InvocationOnMock invocation) throws Throwable {
                return new ValueMapDecorator(new HashMap<String, Object>() {
                    {
                        put(NameConstants.NN_TEMPLATE, "/wrong");

                    }
                });
            }
        });

        GenericList section = adaptToGenericList(wrongPage);
        assertNull(section);
    }

    protected GenericList adaptToGenericList(Page page) {
        return adapterFactory.getAdapter(page, GenericList.class);
    }

    @Test
    public void test_that_adapting_page_with_null_template_returns_null() {
        Page wrongPage = mock(Page.class);
        when(wrongPage.getProperties()).thenAnswer(new Answer<ValueMap>() {
            public ValueMap answer(InvocationOnMock invocation) throws Throwable {
                return new ValueMapDecorator(new HashMap<String, Object>());
            }
        });

        GenericList section = adaptToGenericList(wrongPage);
        assertNull(section);
    }
}
