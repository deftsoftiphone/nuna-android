package com.demo.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.multidex.MultiDex
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.androidnetworking.AndroidNetworking
import com.banuba.sdk.arcloud.di.ArCloudKoinModule
import com.banuba.sdk.core.domain.TrackData
import com.banuba.sdk.core.ui.ContentFeatureProvider
import com.banuba.videoeditor.NunaVideoEditorModule
import com.demo.R
import com.demo.activity.DashboardActivityViewModalFactory
import com.demo.all_hashtags.AllHashTagPostsViewModelFactory
import com.demo.create_post.CreatePostViewModalFactory
import com.demo.dashboard_search.DashboardViewModelFactory
import com.demo.dataSources.discover.DiscoverDataSource
import com.demo.dataSources.discover.DiscoverDataSourceImpl
import com.demo.dataSources.hashtag.HashTagDataSource
import com.demo.dataSources.hashtag.HashTagDataSourceImpl
import com.demo.dataSources.login.LoginDataSource
import com.demo.dataSources.login.LoginDataSourceImpl
import com.demo.dataSources.notifications.NotificationDataSource
import com.demo.dataSources.notifications.NotificationDataSourceImpl
import com.demo.dataSources.post.PostDataSource
import com.demo.dataSources.post.PostDataSourceImpl
import com.demo.dataSources.profile.ProfileDataSource
import com.demo.dataSources.profile.ProfileDataSourceImpl
import com.demo.dataSources.search.SearchDataSource
import com.demo.dataSources.search.SearchDataSourceImpl
import com.demo.dataSources.viewPost.ViewPostDataSource
import com.demo.dataSources.viewPost.ViewPostDataSourceImpl
import com.demo.edit_profile.EditProfileEntityViewModelFactory
import com.demo.edit_profile.EditProfileViewModelFactory
import com.demo.hashtag_tab.HashTagsTabViewModalFactory
import com.demo.language_select.LanguageSelectViewModelFactory
import com.demo.login.LoginViewModelFactory
import com.demo.muebert.network.dataSource.MuebertDataSource
import com.demo.muebert.network.dataSource.MuebertDataSourceImpl
import com.demo.muebert.repository.MuebertRepository
import com.demo.muebert.repository.MuebertRepositoryImpl
import com.demo.muebert.view.AwesomeActivityMusicProvider
import com.demo.muebert.viewmodel.AwesomeAudioContentFactory
import com.demo.notifications.NotificationsViewModelFactory
import com.demo.otp.OtpViewModelFactory
import com.demo.profile.ProfileViewModelFactory
import com.demo.providers.aws.AWSProvider
import com.demo.providers.aws.AWSProviderImpl
import com.demo.providers.customError.CustomErrors
import com.demo.providers.customError.CustomErrorsImpl
import com.demo.providers.mediaPlayer.MediaPlayerProvider
import com.demo.providers.mediaPlayer.MediaPlayerProviderImpl
import com.demo.providers.resources.ResourcesProvider
import com.demo.providers.resources.ResourcesProviderImpl
import com.demo.providers.socketio.SocketIO
import com.demo.providers.socketio.SocketIOImpl
import com.demo.repository.hashtag.HashTagRepository
import com.demo.repository.hashtag.HashTagRepositoryImpl
import com.demo.repository.home.HomeRepository
import com.demo.repository.home.HomeRepositoryImpl
import com.demo.repository.login.LoginRepository
import com.demo.repository.login.LoginRepositoryImpl
import com.demo.repository.post.PostRepository
import com.demo.repository.post.PostRepositoryImpl
import com.demo.repository.search.SearchRepository
import com.demo.repository.search.SearchRepositoryImpl
import com.demo.repository.viewPost.ViewPostRepository
import com.demo.repository.viewPost.ViewPostRepositoryImpl
import com.demo.search.FollowingFollowersViewModelfactory
import com.demo.search.SearchViewModelFactory
import com.demo.setting.SettingsViewModelFactory
import com.demo.util.Util
import com.demo.viewPost.home.viewModal.ViewPostViewModalFactory
import com.demo.webservice.APIService
import com.example.androidphonepermission.helper.AppSignatureHelper
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module


class MainApplication : Application(), KodeinAware {

    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@MainApplication))

        bind() from singleton { APIService() }
        bind() from singleton { com.demo.muebert.network.APIService() }

        //Data Sources
        bind<LoginDataSource>() with singleton {
            LoginDataSourceImpl(instance(), instance(), instance())
        }
        bind<PostDataSource>() with singleton {
            PostDataSourceImpl(instance(), instance())
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
        bind<ViewPostDataSource>() with singleton {
            ViewPostDataSourceImpl(instance(), instance())
        }
        bind<MuebertDataSource>() with singleton {
            MuebertDataSourceImpl(instance(), instance())
        }

        //Providers
        getCurrentActivity()?.let {
            bind<ResourcesProvider>() with provider {
                ResourcesProviderImpl(it)
            }
        }
        bind<CustomErrors>() with provider {
            CustomErrorsImpl(instance())
        }
        bind<SocketIO>() with provider {
            SocketIOImpl(instance())
        }

        bind<MediaPlayerProvider>() with provider {
            MediaPlayerProviderImpl(instance())
        }

        bind<AWSProvider>() with provider {
            AWSProviderImpl(instance())
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
        bind() from singleton {
            AllHashTagPostsViewModelFactory(
                instance(),
                instance(),
            )
        }
        bind() from singleton { NotificationsViewModelFactory(instance(), instance()) }
        bind() from singleton {
            ViewPostViewModalFactory(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind() from singleton { AwesomeAudioContentFactory(instance(), instance()) }
        bind() from singleton { DashboardActivityViewModalFactory(instance(), instance()) }
        bind() from singleton {
            SearchViewModelFactory(
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
        bind() from singleton { SettingsViewModelFactory(instance(), instance(), instance()) }


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
        bind<ViewPostRepository>() with singleton {
            ViewPostRepositoryImpl(instance())
        }

        bind<MuebertRepository>() with singleton {
            MuebertRepositoryImpl(instance())
        }
    }

    var localizationDelegate =
        LocalizationApplicationDelegate(this)

    companion object {
        private lateinit var instance: MainApplication
        fun get(): MainApplication = instance
        var simpleCache: SimpleCache? = null
        var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor? = null
        var exoDatabaseProvider: ExoDatabaseProvider? = null
        var exoPlayerCacheSize: Long = 1024 * 1024 * 1024
    }

    private var mCurrentActivity: Activity? = null

    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun getNetworkSpeed(): Double {
        if(Util.checkIfHasNetwork()){
            val cm =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            // Network Capabilities of Active Network
            val nc = cm.getNetworkCapabilities(cm.activeNetwork)
            // DownSpeed in MBPS
            return (nc?.linkDownstreamBandwidthKbps)!! / 1024.0
        }else{
            return 0.0
        }
    }

    fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.mCurrentActivity = mCurrentActivity
    }

    val musicProviderModule = module {
        factory<ContentFeatureProvider<TrackData>>(named("musicTrackProvider"), override = true) {
            AwesomeActivityMusicProvider()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AndroidNetworking.initialize(applicationContext)
        initBanubaModule()
        val appSignature = AppSignatureHelper(this)
        appSignature.getAppSignatures()
        initCalligraphy()
        initCache()
    }

    private fun initCalligraphy() {
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/GothamPro.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
    }

    private fun initBanubaModule() {
        startKoin {
            androidContext(this@MainApplication)
            modules(
                listOf(
                    NunaVideoEditorModule().module,
                    ArCloudKoinModule().module, musicProviderModule
                )
            )
        }
    }

    private fun initCache() {
        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor =
                LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
        }
        if (exoDatabaseProvider == null) {
            exoDatabaseProvider = ExoDatabaseProvider(this)
        }

        if (simpleCache == null) {
            simpleCache = SimpleCache(
                cacheDir,
                leastRecentlyUsedCacheEvictor!!,
                exoDatabaseProvider!!
            )
            /*if (simpleCache!!.cacheSpace >= 400207768) {
                freeMemory()
            }*/
        }
    }

    fun offlineDeactivatedUser() {
        (mCurrentActivity as BaseActivity).invokeSocketEvent(
            (mCurrentActivity as BaseActivity).resource.getString(
                R.string.socket_user_logout
            )
        )
    }

    fun getContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }
}