package com.example.weatherapp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout.TabGravity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// dc07a8df80e4cf4c105dcc4a2948b462
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchweatherdata("Jaipur")
        Searchcity()
    }

    private fun Searchcity()
    {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query!=null) {
                    fetchweatherdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

      private fun fetchweatherdata(Cityname : String) {
          val retrofit = Retrofit.Builder()
              .addConverterFactory(GsonConverterFactory.create())
              .baseUrl("https://api.openweathermap.org/data/2.5/")
              .build().create(Apiinterface::class.java)

          val response = retrofit.getweatherdata(Cityname, "dc07a8df80e4cf4c105dcc4a2948b462","metric")
          response.enqueue(object :Callback<WeatherApp>{

              override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                  val responseBody = response.body()
                  if(response.isSuccessful && responseBody!=null)
                  {
                      val temperature = responseBody.main.temp.toString()
                      val humidity = responseBody.main.humidity
                      val windSpeed = responseBody.wind.speed
                      val sunRise = responseBody.sys.sunrise.toLong()
                      val sunSet = responseBody.sys.sunset.toLong()
                      val seaLevel = responseBody.main.pressure
                      val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                      val maxTemp = responseBody.main.temp_max
                      val minTemp = responseBody.main.temp_min
                      binding.temp.text="$temperature °C"
                      binding.weather.text = condition
                      binding.maxtemp.text = "Max Temp: $maxTemp°C"
                      binding.mintemp.text = "Min Temp: $minTemp°C"
                      binding.Humidity.text = "$humidity %"
                      binding.windspeed.text = "$windSpeed m/s"
                      binding.sunrise.text ="${time(sunRise)}"
                      binding.sunset.text ="${time(sunSet)}"
                      binding.Sea.text = "$seaLevel hPa"
                      binding.Condition.text = condition
                      binding.Day.text = dayName(System.currentTimeMillis())
                      binding.date.text = date()
                      binding.cityname.text ="$Cityname"



//                      Log.d("TAG", "onResponse: $temperature")
                      changeimageaccordingtoweatherchange(condition)

                  }
              }
              override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                  TODO("Not yet implemented")
              }

          })

      }

    private fun changeimageaccordingtoweatherchange(conditions : String) {
        when(conditions)
        {
            "Haze" , "Partly Clouds" , "Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear sky " , "sunny" , " Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain" , "Dizzle" , "Moderate Rain" , "Showers" , "Heavy rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light snow" , "Moderate Snow" , "Heavy Snow", "Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp:  Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }


    fun dayName(timestamp:  Long) : String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
  }

