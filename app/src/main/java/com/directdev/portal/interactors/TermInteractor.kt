package com.directdev.portal.interactors

import com.directdev.portal.models.TermModel
import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.TermRepository
import com.directdev.portal.repositories.TimeStampRepository
import io.reactivex.Single
import io.realm.Realm
import javax.inject.Inject
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 9/3/17.
 *------------------------------------------------------------------------------------------------*/
class TermInteractor @Inject constructor(
        private val termRepo: TermRepository,
        @Named("term") private val timeStampRepo: TimeStampRepository,
        private val bimayApi: NetworkHelper
) {
    fun getTerms(): List<Int> {
        val savedTerms = Realm.getDefaultInstance().use {
            it.where(TermModel::class.java)
                    .findAll()
                    .sort("value")
                    .map { term -> term.value }
        }
        return if (savedTerms.isEmpty()) calculateTerm() else savedTerms
    }

    private fun calculateTerm(): List<Int> {
        val dateString = timeStampRepo.today().toString()
        val year = dateString.takeLast(2) + "10"
        return arrayListOf(year.toInt())
    }

    fun sync(cookie: String): Single<String> = bimayApi.getTerms(cookie).map {
        termRepo.save(it)
        cookie
    }

    fun getSemesterName(chosenTerm: Int): String {
        val year = ((chosenTerm + 99) / 100) - ((termRepo.getFirstTerm() + 99) / 100)
        return "Semester " + when (chosenTerm.toString().substring(2)) {
            "10" -> ((year * 2) + 1).toString()
            "20" -> ((year * 2) + 2).toString()
            "30" -> ((year * 2) + 2).toString() + " ( SP )"
            else -> "N/A"
        }
    }
}