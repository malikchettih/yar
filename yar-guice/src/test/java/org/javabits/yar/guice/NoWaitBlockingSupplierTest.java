package org.javabits.yar.guice;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.javabits.yar.*;
import org.junit.Test;

import javax.annotation.Nullable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.javabits.yar.SupplierEvent.Type.ADD;

/**
 * @author Romain Gilles
 *         Date: 5/31/13
 *         Time: 7:39 PM
 */
public class NoWaitBlockingSupplierTest {

    private final Id<MyInterface> id = Ids.newId(MyInterface.class);

    @Test
    public void testDirectGetNull() throws Exception {
        BlockingSupplier<MyInterface> supplier = new NoWaitBlockingSupplier<>(id, null);
        assertThat(supplier.get(), is(nullValue()));
    }

    @Test
    public void testDirectInitNullAndThenSet() throws Exception {
        final Boolean[] set = {null};
        ListeningExecutorService listeningExecutorService = MoreExecutors.sameThreadExecutor();
        final NoWaitBlockingSupplier<MyInterface> supplier = new NoWaitBlockingSupplier<>(id, null);
        assertThat(supplier.get(), is(nullValue()));
        supplier.getAsync().addListener(new Runnable() {
            @Override
            public void run() {
                set[0] = supplier.get() != null;
            }
        }, listeningExecutorService);
        assertThat(set[0], is(nullValue()));
        supplier.supplierChanged(new SupplierEvent(ADD, new Supplier<MyInterface>() {

            @Override
            public Id<MyInterface> id() {
                return id;
            }

            @Nullable
            @Override
            public MyInterface get() {
                return new MyInterfaceImpl();
            }
        }));
        assertThat(set[0], is(not(nullValue())));
    }

    @Test
    public void testDirectGetNonNull() throws Exception {
        final MyInterfaceImpl myInterface = new MyInterfaceImpl();
        BlockingSupplier<MyInterface> supplier = new NoWaitBlockingSupplier<>(id, new Supplier<MyInterface>() {

            @Override
            public Id<MyInterface> id() {
                return id;
            }

            @Nullable
            @Override
            public MyInterface get() {
                return myInterface;
            }
        });
        assertThat(supplier.get(), is((MyInterface) myInterface));
    }
}
