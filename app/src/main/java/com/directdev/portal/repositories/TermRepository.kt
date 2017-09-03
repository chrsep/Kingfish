package com.directdev.portal.repositories

import com.directdev.portal.models.TermModel
import io.realm.Realm
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/29/17.
 *------------------------------------------------------------------------------------------------*/
class TermRepository @Inject constructor(
        private val timeStampRepo: TimeStampRepository
) {
    fun getTerms(): List<Int> {
        val savedTerms = Realm.getDefaultInstance().use {
            it.where(TermModel::class.java)
                    .findAllSorted("value")
                    .map { term -> term.value }
        }
        return if (savedTerms.isEmpty()) calculateTerm() else savedTerms
    }

    private fun calculateTerm(): List<Int> {
        val dateString = timeStampRepo.today().toString()
        val year = dateString.takeLast(2) + "10"
        return arrayListOf(year.toInt())
    }

    fun save(terms: List<TermModel>) = Realm.getDefaultInstance().use {
        it.executeTransaction {
            it.insertOrUpdate(terms)
        }
    }

    fun getLatestTerm(): Int = Realm.getDefaultInstance().use {
        it.where(TermModel::class.java)
                .findAllSorted("value")
                .map { it.value }
                .last()
    }
}
