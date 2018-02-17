package com.directdev.portal.interactors

import com.directdev.portal.models.CourseModel
import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.ResourceRepository
import io.reactivex.Single
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 10/21/17.
 *------------------------------------------------------------------------------------------------*/
class ResourceInteractor @Inject constructor(
        private val resourceRepo: ResourceRepository,
        private val bimayApi: NetworkHelper
) {
    fun getResource(classNumber: Int) = resourceRepo.getResources(classNumber)
    //TODO: SYNC NEEDS TO HANDLE MULTIPLE COURSE AT ONCE
    fun sync(cookies: String, course: CourseModel?): Single<Unit>? {
        return if (course != null)
            bimayApi.getResources(cookies, course).map { resourceRepo.save(it) }
        else
            Single.fromCallable { Unit }
    }

/* TODO: CLEAN THIS UP
    fun fetchResources(data: RealmResults<CourseModel>): Single<Unit> {
        isActive = true
        val cookie = ctx.readPref(R.string.cookie, "")
        return Single.zip(data.map {
            val classNumber = it.classNumber
            api.getResources(
                    it.courseId,
                    it.crseId,
                    it.term.toString(),
                    it.ssrComponent,
                    it.classNumber.toString(),
                    cookie
            ).map { data ->
                data.classNumber = classNumber
                data
            }.subscribeOnIo()
        }) { resources ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction { realm ->
                resources.forEach {
                    val resModel = ResModel()
                    resModel.book.addAll((it as ResModelIntermidiary).book)
                    resModel.path.addAll(it.path)
                    resModel.resources.addAll(it.resources)
                    resModel.url.addAll(it.url)
                    resModel.webContent = it.webContent
                    resModel.classNumber = it.classNumber
                    realm.insertOrUpdate(resModel)
                }
            }
            realm.close()
        }.defaultThreads().doOnSubscribe {
            isActive = true
        }.doAfterTerminate {
            isActive = false
        }
    }*/

}