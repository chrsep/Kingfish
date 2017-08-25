package com.directdev.portal.interactors

import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.FlagRepository
import com.directdev.portal.repositories.UserCredRepository
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.Response

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/20/17.
 *------------------------------------------------------------------------------------------------*/

class AuthInteractorUnitTest {

    @Test
    fun testExecute() {
        // Given
        val bimayApi: NetworkHelper = mock {
            on { getIndexHtml() } doReturn Single.just(Response.success(ResponseBody.create(MediaType.parse("test"), indexHtml)))
            on { getRandomizedFields(any(), any()) } doReturn Single.just(Response.success(ResponseBody.create(MediaType.parse("text/js"), loaderJS)))
            on { authenticate(any(), any()) } doReturn Single.just(Response.success("Success"))
        }
        val userCredRepo: UserCredRepository = mock { }
        val flagRepo: FlagRepository = mock { }
        val authInteractor = AuthInteractor(bimayApi, userCredRepo, flagRepo)

        // When
        authInteractor.execute("test", "test").subscribe({}, { throw it })

        // Then

    }

    private val indexHtml = """
    <!DOCTYPE HTML>
    <html lang="en">
        <head>

            <title>Binusian Login</title>
            <meta name="viewport" content="width=device-width, initial-scale=1">

            <!-- Favicon -->
            <link rel="icon" type="image/png" href="images/favicon.ico">

            <!-- Stylesheet -->
            <link rel="stylesheet" id="DefaultCssMainSite" type="text/css" href="">
            <link rel="stylesheet" id="CssMainSite" type="text/css" href="">
            <link rel="stylesheet" id="CssMainStyle" type="text/css" href="">
            <link rel="stylesheet" id="CssAddStyle" type="text/css" href="">
            <link rel="stylesheet" type="text/css" href="css/collabees.css?ver=1.5.5">

            <!-- JavaScript -->
            <script src="../asset/js/jquery.js"></script>
            <script type="text/javascript" src="../asset/js/config.js"></script>
            <script src="../login/js/script.login.js"></script>
            <script type="text/javascript" src="../asset/js/jquery.fancybox.js"></script>
            <script type="text/javascript" src="../asset/js/script.js"></script>
            <script type="text/javascript" src="../asset/js/plugins/combobox.js"></script>

            <!--[if lt IE 9]>
                <script src="js/html5shiv.js"></script>
            <![endif]-->


            <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
            <script src="../login/loader.php?serial=dkFEd4MynSdfeZtOdF4if2gaPbOA0dtzjnR16At6IJg%3D"></script>

        </head>

        <body>
            <div id="page" class="main-container">

                <section class="background">
                    <div class="overlay"></div>
                </section>

                <section class="wrapper">
                    <div id="login" class="login" style="width:385px;">
                        <div class="login-head">
                            <div class="ribbon"></div>
                            <div class="logo">
                                <span class="site-name">
                                    <img id="imgLogo" src="" alt="Logo">
                                </span>
                                <span class="site-description">
                                    People Innovation Excellence
                                </span>
                            </div>
                        </div>

                        <form class="custom-form" action="sys_login.php" method="post" style="padding: 20px 40px 10px;" autocomplete="off">
                                                <div class="user-input">
                                <label class="suffix-wrapper show">
                                    <span class="icon-wrap">
                                        <i class="icon icon-user"></i>
                                    </span>
                                    <input type="text" name="AUF%2Fvgbw33MTkBo7rD72x%2Fx5RwK1MUfUBV2RWbocfPY%3D" class = "input text" placeholder="Username">
                                    <span class="email-suffix suffix">@binus.ac.id</span>
                                </label>
                            </div>
                            <p>
                                <span class="custom-textbox">
                                    <span class="icon-wrap">
                                        <i class="icon icon-pass"></i>
                                    </span>
                                    <input type="password" name="8q1BWUjqn0bbO329Fr0GaP5ChR%2FcPDRHq5n8xyYnA88%3D" placeholder="Password">
                                </span>
                            </p>
                            <p>
                                <div id="html_element"></div>
                            </p>
                            <p>
                                <input type="submit" name="ctl00${"$"}ContentPlaceHolder1${"$"}SubmitButtonBM" value="Login" id="ctl00_ContentPlaceHolder1_SubmitButtonBM" class="button button-primary wide">
                                <span style="font-size: x-small;">Need Help? Click <a target="_blank" href="https://newcontent.binus.ac.id/data_content/download/student_support/How%20to%20Login%20BINUSMAYA.pdf">Here</a></span>
                            </p>
                        </form>

                        <div class="login-footer">
                            <div class="bottom-nav">
                                <a id="register" class="act-button" href="getusername.php">Get Your Username</a>
                            </div>
                            <div class="bottom-nav"><a class="act-button" href="forgotpassword.php">Forgot Your Password</a></div>
                        </div>
                    </div>
                </section>

            </div>
        </body>
    </html>
    """.trimIndent()

    private val loaderJS = """
    ${'$'}.noConflict();${'$'}(document).ready(function(){${'$'}.ajax({url:'https://binusmaya.binus.ac.id/services/ci/index.php/support/sitemanagement/getSiteDataBySite',type:'POST',dataType:'json',data:JSON.stringify({site:'https://binusmaya.binus.ac.id'}),contentType:'application/json;charset=utf-8',async:false,success:function(data){if(data.length>0)
    {${'$'}('#DefaultCssMainSite').prop('href','../asset/css/login.css');if(data[0].CssMainSite!='')
    ${'$'}('#CssMainSite').prop('href','../asset/themes/'+data[0].CssMainSite+'login.css');${'$'}('#imgLogo').prop('src',data[0].LogoUniversity!=''?data[0].LogoUniversity:'../asset/images/logo.png');${'$'}('#CssMainStyle').prop('href','../asset/themes/bm5/css/style.css');${'$'}('#CssAddStyle').prop('href','../asset/themes/'+data[0].CssMainSite+'style.css');}}});${'$'}('.custom-combobox').binus_combobox();});var onloadCallback=function(){grecaptcha.render('html_element',{'sitekey':'6LdbuSUUAAAAAMuiVhPeuIElQW09iPA53qMuSrDQ'});};${'$'}(document).ready(function(){${'$'}('.custom-form').off('submit').on('submit',function(eventObj){${'$'}(this).append('<input type="hidden" name="fI_PGiboLkDRjKDADWMH1dCn77G7MraxgfTGd4R2S1Q%3D" value="FOVODfD8_sk1hhu6T0xi%2F3x49gUonQcyLIl160Za9EE%3D" />');${'$'}(this).append('<input type="hidden" name="TJfJYA4bAV3J%2FYSk4n0K_SPKlNkds_Kln8yFPsIuM9E24R877Ze2iEtDDJH0LB7uuWkGrXDYr4SEQsq1vbOiRw%3D%3D" value="Dge3W3adgIvq3QJFeJl0frgfwmSG9F3j_IiDim4b8pdY1Lxp68rGcDHmpwLEYCvOTNm%2F9ry89FcRYgyKzUrDUd79W63HNaz3U1G059sTxIqa5hr5e3jHdi4wTQiE4358" />');return true;});${'$'}('.login').css('min-height','360px');});
    """.trimIndent()
}