package com.demo.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.demo.R
import com.demo.all_hashtags.AllHashTagPostsViewModelFactory
import com.demo.create_post.CreatePostViewModalFactory
import com.demo.dashboard_search.DashboardViewModelFactory
import com.demo.dataSources.discover.DiscoverDataSource
import com.demo.dataSources.discover.DiscoverDataSourceImpl
import com.demo.dataSources.discover.NotificationDataSource
import com.demo.dataSources.discover.NotificationDataSourceImpl
import com.demo.dataSources.hashtag.HashTagDataSource
import com.demo.dataSources.hashtag.HashTagDataSourceImpl
import com.demo.dataSources.login.LoginDataSource
import com.demo.dataSources.login.LoginDataSourceImpl
import com.demo.dataSources.post.PostDataSource
import com.demo.dataSources.post.PostDataSourceImpl
import com.demo.dataSources.profile.ProfileDataSource
import com.demo.dataSources.profile.ProfileDataSourceImpl
import com.demo.dataSources.search.SearchDataSource
import com.demo.dataSources.searchNotificationDataSourceImpl.SearchDataSourceImpl
import com.demo.edit_profile.EditProfileEntityViewModelFactory
import com.demo.edit_profile.EditProfileViewModelFactory
import com.demo.hashtag_tab.HashTagsTabViewModalFactory
import com.demo.language_select.LanguageSelectViewModelFactory
import com.demo.login.LoginViewModelFactory
import com.demo.notifications.NotificationsViewModelFactory
import com.demo.otp.OtpViewModelFactory
import com.demo.profile.ProfileViewModelFactory
import com.demo.providers.customError.CustomErrors
import com.demo.providers.customError.CustomErrorsImpl
import com.demo.providers.resources.ResourcesProvider
import com.demo.providers.resources.ResourcesProviderImpl
import com.demo.providers.socketio.SocketIO
import com.demo.providers.socketio.SocketIOImpl
import com.demo.repository.hashtag.HashTagRepository
import com.demo.repository.hashtag.HashTagRepositoryImpl
import com.demo.repository.hashtag.SearchRepository
import com.demo.repository.home.HomeRepository
import com.demo.repository.home.HomeRepositoryImpl
import com.demo.repository.login.LoginRepository
import com.demo.repository.login.LoginRepositoryImpl
import com.demo.repository.post.PostRepository
import com.demo.repository.post.PostRepositoryImpl
import com.demo.repository.search.SearchRepositoryImpl
import com.demo.search.FollowingFollowersViewModelfactory
import com.demo.search.SearchViewModelFactory
import com.demo.setting.SettingsViewModelFactory
import com.demo.util.aws.AmazonUtil
import com.demo.webservice.APIService
import com.example.androidphonepermission.helper.AppSignatureHelper
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


class MainApplication : Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@MainApplication))

        bind() from singleton { APIService() }

        //Data Sources
        bind<LoginDataSource>() with singleton {
            LoginDataSourceImpl(instance(), instance(), instance())
        }
        bind<PostDataSource>() with singleton {
            PostDataSourceImpl(instance(), instance(), instance())
        }
        bind<DiscoverDataSource>() with singleton {
            DiscoverDataSourceImpl(instance(), instance())
        }
        bind<ProfileDataSource>() with singleton {
            ProfileDataSourceImpl(instance(), instance())
        }
        bind<HashTagDataSource>() with singleton {
            HashTagDataSourceImpl(instance(), instance())
        }
        bind<NotificationDataSource>() with singleton {
            NotificationDataSourceImpl(instance(), instance())
        }
        bind<SearchDataSource>() with singleton {
            SearchDataSourceImpl(instance(), instance())
        }


        //Providers
        bind<ResourcesProvider>() with provider {
            ResourcesProviderImpl(instance())
        }
        bind<CustomErrors>() with provider {
            CustomErrorsImpl(instance())
        }
        bind<SocketIO>() with provider {
            SocketIOImpl()
        }


        //Factory
        bind() from singleton { LanguageSelectViewModelFactory(instance()) }
        bind() from singleton { LoginViewModelFactory(instance()) }
        bind() from singleton { OtpViewModelFactory(instance(), instance()) }
        bind() from singleton { CreatePostViewModalFactory(instance(), instance()) }
        bind() from singleton { DashboardViewModelFactory(instance(), instance(), instance()) }
        bind() from singleton { ProfileViewModelFactory(instance(), instance()) }
        bind() from singleton { EditProfileViewModelFactory(instance()) }
        bind() from singleton { EditProfileEntityViewModelFactory(instance()) }
        bind() from singleton { HashTagsTabViewModalFactory(instance(), instance()) }
        bind() from singleton { AllHashTagPostsViewModelFactory(instance(), instance()) }
        bind() from singleton { NotificationsViewModelFactory(instance(), instance()) }
        bind() from singleton {
            SearchViewModelFactory(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind() from singleton {
            FollowingFollowersViewModelfactory(
                instance(),
                instance(),
                instance()
            )
        }
        bind() from singleton { SettingsViewModelFactory(instance()) }


        //Repositories
        bind<LoginRepository>() with provider {
            LoginRepositoryImpl(instance())
        }
        bind<PostRepository>() with provider {
            PostRepositoryImpl(instance())
        }
        bind<HomeRepository>() with provider {
            HomeRepositoryImpl(instance(), instance(), instance())
        }
        bind<HashTagRepository>() with provider {
            HashTagRepositoryImpl(instance(), instance())
        }
        bind<SearchRepository>() with singleton {
            SearchRepositoryImpl(instance())
        }
    }

    var localizationDelegate =
        LocalizationApplicationDelegate(this)

    companion object {
        private lateinit var instance: MainApplication
        fun get(): MainApplication = instance
    }

    private var mCurrentActivity: Activity? = null


    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.mCurrentActivity = mCurrentActivity
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CalligraphyConfig.initDefault(
            CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/normal_pro.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
        // fontPath="fonts/Roboto-Bold.ttf"
        AmazonUtil.initAmazonClient()
        var appSignature = AppSignatureHelper(this)
        appSignature.getAppSignatures()
        //  registerReceiver(TransferNetworkLossHandler.getInstance(getApplicationContext()),  IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    fun getContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(localizationDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        localizationDelegate.onConfigurationChanged(this)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

}