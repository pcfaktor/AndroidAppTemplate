package com.softteco.template.ui.feature.profile

import androidx.lifecycle.viewModelScope
import com.softteco.template.data.base.error.ErrorEntity
import com.softteco.template.data.base.error.Result
import com.softteco.template.data.profile.ProfileRepository
import com.softteco.template.data.profile.entity.Profile
import com.softteco.template.utils.setTestDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

class ProfileViewModelTest : Spek({

    setTestDispatcher()

    Feature("Profile") {

        lateinit var profileRepository: ProfileRepository
        lateinit var viewModel: ProfileViewModel

        Scenario("when screen just opened") {

            val profile = Profile(id = UUID.randomUUID().toString(), name = "John Doe")

            Given("viewModel is initialized") {
                profileRepository = object : ProfileRepository {
                    override suspend fun getApi(): Result<String> = Result.Success("")
                    override suspend fun getUser(id: String): Result<Profile> {
                        delay(1.seconds)
                        return Result.Success(profile)
                    }
                }
                viewModel = ProfileViewModel(profileRepository)
            }

            When("data not yet received") {}

            Then("the State has initial state") {
                assertThat(viewModel.state.value, equalTo(ProfileViewModel.State()))
            }
            And("the loading indicator is shown") {
                viewModel.viewModelScope.launch {
                    viewModel.state.collect()
                }
                assertThat(viewModel.state.value.loading, `is`(true))
            }
        }

        Scenario("when profile received") {

            val profile = Profile(id = UUID.randomUUID().toString(), name = "John Doe")

            Given("viewModel is initialized") {
                profileRepository = object : ProfileRepository {
                    override suspend fun getApi(): Result<String> = Result.Success("")
                    override suspend fun getUser(id: String): Result<Profile> =
                        Result.Success(profile)
                }
                viewModel = ProfileViewModel(profileRepository)
            }

            When("data received") {
                viewModel.viewModelScope.launch {
                    viewModel.state.collect()
                }
            }

            Then("the screen state contains profile object") {
                assertThat(viewModel.state.value.profile, equalTo(profile))
            }
        }

        Scenario("when greeting received") {

            val greeting = "Hello World"

            Given("viewModel is initialized") {
                profileRepository = object : ProfileRepository {
                    override suspend fun getApi(): Result<String> = Result.Success(greeting)
                    override suspend fun getUser(id: String): Result<Profile> =
                        Result.Error(ErrorEntity.Unknown)
                }
                viewModel = ProfileViewModel(profileRepository)
            }

            When("data received") {
                viewModel.viewModelScope.launch {
                    viewModel.state.collect()
                }
            }

            Then("the screen state contains profile object") {
                assertThat(viewModel.state.value.greeting, equalTo(greeting))
            }
        }

        Scenario("when network error happens") {

            Given("viewModel is initialized") {
                profileRepository = object : ProfileRepository {
                    override suspend fun getApi(): Result<String> = Result.Error(
                        ErrorEntity.Network
                    )

                    override suspend fun getUser(id: String): Result<Profile> = Result.Error(
                        ErrorEntity.Network
                    )
                }
                viewModel = ProfileViewModel(profileRepository)
            }

            When("Network") {
                viewModel.viewModelScope.launch {
                    viewModel.state.collect()
                }
            }

            Then("snackbar is shown") {
                assertThat(viewModel.state.value.snackbar.show, `is`(true))
            }
        }
    }
})
