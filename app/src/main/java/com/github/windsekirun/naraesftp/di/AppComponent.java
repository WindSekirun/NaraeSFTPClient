package com.github.windsekirun.naraesftp.di;

import com.github.windsekirun.baseapp.di.module.BaseBindsModule;
import com.github.windsekirun.baseapp.di.module.BaseInterceptorModule;
import com.github.windsekirun.baseapp.di.module.BaseProvidesModule;
import com.github.windsekirun.daggerautoinject.ActivityModule;
import com.github.windsekirun.daggerautoinject.FragmentModule;
import com.github.windsekirun.daggerautoinject.ServiceModule;
import com.github.windsekirun.daggerautoinject.ViewModelModule;
import com.github.windsekirun.naraesftp.MainApplication;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                AppProvidesModule.class,
                BaseProvidesModule.class,
                AppBindsModule.class,
                BaseBindsModule.class,
                BaseInterceptorModule.class,
                ActivityModule.class,
                FragmentModule.class,
                ViewModelModule.class,
                ServiceModule.class
        }
)
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(MainApplication application);

        AppComponent build();
    }

    void inject(MainApplication mainApp);

}