/*
 * Copyright (c) 9/29/14 9:34 AM Romain Gilles
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.javabits.yar.guice.osgi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.javabits.yar.guice.YarGuices.builder;

import org.javabits.yar.BlockingSupplierRegistry;
import org.javabits.yar.Id;
import org.javabits.yar.Ids;
import org.javabits.yar.Registry;
import org.javabits.yar.guice.ExecutionStrategy;
import org.javabits.yar.guice.YarGuices;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;

import java.util.function.Supplier;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BundleRegistryTest {
    public static final Id<MyInterface> ID = Ids.newId(MyInterface.class);
    public static final Supplier<MyInterface> INSTANCE_SUPPLIER = ()-> new MyInterface() {
    };
    @Mock
    private Bundle bundle;

    private BlockingSupplierRegistry blockingSupplierRegistry;
    private BundleRegistry registry;

    @Before
    public void setup() {
        YarGuices.Builder builder = builder();
        builder.listenerUpdateExecutionStrategy(ExecutionStrategy.Type.SAME_THREAD);
        blockingSupplierRegistry = builder.build();
        registry = new BundleRegistry(blockingSupplierRegistry, bundle);

    }

    @Test
    public void testGetNativeSupplier() {
        registry.put(ID, INSTANCE_SUPPLIER);
        OSGiSupplier<MyInterface> supplier = registry.get(ID);
        assert supplier != null;
        assertThat(supplier.getNativeSupplier(), is(INSTANCE_SUPPLIER));
    }

    @Test
    public void testGetBundle() {
        registry.put(ID, INSTANCE_SUPPLIER);
        OSGiSupplier<MyInterface> supplier = registry.get(ID);
        assert supplier != null;
        assertThat(supplier.getBundle(), is(bundle));
    }


    @Test
    public void testGetAllWithBundle() {
        registry.put(ID, INSTANCE_SUPPLIER);
        List<org.javabits.yar.Supplier<MyInterface>> suppliers = registry.getAll(ID);
        assertThat(((BundleSupplier) suppliers.get(0)).getBundle(), is(bundle));
    }

    @Test
    public void testGetAllWithoutBundle() {
        blockingSupplierRegistry.put(ID, INSTANCE_SUPPLIER);
        List<org.javabits.yar.Supplier<MyInterface>> suppliers = registry.getAll(ID);
        assertThat(((BundleSupplier) suppliers.get(0)).getBundle(), is(nullValue()));
    }

    @Test
    public void testGetAllAware() {
        MyImplRegistryAware aware = new MyImplRegistryAware();
        registry.put(ID, () -> aware);
        List<org.javabits.yar.Supplier<MyInterface>> suppliers = registry.getAll(ID);
        MyImplRegistryAware myImplRegistryAware = (MyImplRegistryAware) suppliers.get(0).get();
        assert myImplRegistryAware != null;
        assertThat(myImplRegistryAware.getBundle(), is(bundle));
        assertThat(myImplRegistryAware.getRegistry(), is((Registry) registry));
        assertThat(myImplRegistryAware.getBlockingSupplierRegistry(), is((BlockingSupplierRegistry) registry));
        assertThat(myImplRegistryAware.getOsgiRegistry(), is((OSGiRegistry) registry));
    }

    @Test
    public void testGetSingleAware() {
        MyImplRegistryAware aware = new MyImplRegistryAware();
        registry.put(ID, () -> aware);
        org.javabits.yar.Supplier<MyInterface> supplier = registry.get(ID);
        assert supplier != null;
        MyImplRegistryAware myImplRegistryAware = (MyImplRegistryAware) supplier.get();
        assert myImplRegistryAware != null;
        assertThat(myImplRegistryAware.getBundle(), is(bundle));
        assertThat(myImplRegistryAware.getRegistry(), is((Registry) registry));
        assertThat(myImplRegistryAware.getBlockingSupplierRegistry(), is((BlockingSupplierRegistry) registry));
        assertThat(myImplRegistryAware.getOsgiRegistry(), is((OSGiRegistry) registry));
    }

    interface MyInterface {
    }

    static class MyImplRegistryAware implements MyInterface, BundleAware, RegistryAware, BlockingSupplierRegistryAware, OSGiRegistryAware {

        private BlockingSupplierRegistry blockingSupplierRegistry;
        private Bundle bundle;
        private OSGiRegistry osgiRegistry;
        private Registry registry;

        public BlockingSupplierRegistry getBlockingSupplierRegistry() {
            return blockingSupplierRegistry;
        }

        public Bundle getBundle() {
            return bundle;
        }

        public OSGiRegistry getOsgiRegistry() {
            return osgiRegistry;
        }

        public Registry getRegistry() {
            return registry;
        }

        @Override
        public void setBlockingSupplierRegistry(BlockingSupplierRegistry registry) {
            blockingSupplierRegistry = registry;
        }

        @Override
        public void setBundle(Bundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public void setOSGiRegistry(OSGiRegistry registry) {
            osgiRegistry = registry;
        }

        @Override
        public void setRegistry(Registry registry) {
            this.registry = registry;
        }
    }
}
