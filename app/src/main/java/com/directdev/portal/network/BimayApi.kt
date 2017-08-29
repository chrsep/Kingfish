package com.directdev.portal.network

import com.directdev.portal.models.*
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject


class BimayApi @Inject constructor(override val bimayService: BimayService) : NetworkHelper {
    override fun getTerms(cookie: String): Single<List<TermModel>> = bimayService.getTerms(cookie)

    override fun getJournalEntries(cookie: String, terms: List<Int>): Single<List<JournalModel>> = Single.zip(
            bimayService.getFinances(cookie).subscribeOn(Schedulers.io()),
            bimayService.getSessions(cookie).subscribeOn(Schedulers.io()),
            getExams(cookie, terms.takeLast(3)),
            Function3<List<FinanceModel>, List<SessionModel>, List<ExamModel>, List<JournalModel>> { finance, session, exam ->
                val items = mutableListOf<JournalModel>()
                finance.forEach { items.add(JournalModel(it.dueDate).setDate()) }
                exam.forEach { items.add(JournalModel(it.date).setDate("yyyy-MM-dd")) }
                session.forEach { items.add(JournalModel(it.date).setDate()) }
                items.forEach { item ->
                    session.forEach { if (item.id == it.date) item.session.add(it) }
                    finance.forEach { if (item.id == it.dueDate) item.finance.add(it) }
                    exam.forEach { if (item.id == it.date) item.exam.add(it) }
                }
                items
            })

    override fun getExams(cookie: String, terms: List<Int>): Single<List<ExamModel>> = Single.zip(terms.map {
        bimayService.getExams(cookie, ExamRequestBody(it)).subscribeOn(Schedulers.io())
    }, { exams ->
        val list = mutableListOf<ExamModel>()
        exams.forEach { if (it is List<*>) list.addAll(it as Collection<ExamModel>) }
        list
    })

    override fun getIndexHtml(): Single<Response<ResponseBody>> =
            bimayService.getIndexHtml().subscribeOn(Schedulers.io())

    override fun getLoaderJs(cookie: String, serial: String): Single<Response<ResponseBody>> =
            bimayService.getSerial(cookie, serial).subscribeOn(Schedulers.io())

    override fun authenticate(cookie: String, fieldMap: HashMap<String, String>): Single<Response<String>> =
            bimayService.signIn2(cookie, fieldMap).subscribeOn(Schedulers.io())

    override fun switchRole(cookie: String) = bimayService.switchRole(cookie)

    override fun getUserProfile(cookie: String): Single<ResponseBody> =
            bimayService.getProfile(cookie).subscribeOn(Schedulers.io())
}