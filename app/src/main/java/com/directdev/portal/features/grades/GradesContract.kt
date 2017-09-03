package com.directdev.portal.features.grades

import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView
import com.directdev.portal.models.CreditModel
import com.directdev.portal.models.ScoreModel
import io.realm.RealmResults

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/31/17.
 *------------------------------------------------------------------------------------------------*/
interface GradesContract {
    interface View : BaseView<Presenter> {

        fun logAnalytics()
        fun showGradesRecycler()
        fun hideGradesRecycler()
        fun setGpaGraphData(credits: RealmResults<CreditModel>)
        fun updateRecycler(grades: List<RealmResults<ScoreModel>>, credits: CreditModel)
        fun showLoading()
        fun hideLoading()
        fun setToolbarTitle(title: String)
        fun showSuccess(message: String)
        fun showFailed(message: String)
    }

    interface Presenter : BasePresenter {
        fun switchTerm(term: Int = -1)
        fun onResume()
        fun setToolbarTitle(firstTerm: Int, chosenTerm: Int)
        fun sync(bypass: Boolean = false)
        fun onStop()
        fun onStart()
    }
}