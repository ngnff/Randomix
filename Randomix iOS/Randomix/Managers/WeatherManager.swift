//
//  WeatherManager.swift
//  Randomix
//
//  Created by Slava on 27/11/2023.
//

import Foundation
import CoreLocation

class WeatherManager
{
    func getCurrentWeather(latitude: CLLocationDegrees, longitude: CLLocationDegrees) async throws -> WeatherData
    {
        guard let url = URL(string: "https://api.openweathermap.org/data/2.5/weather?lat=\(latitude)&lon=\(longitude)&appid=6c33fe627076da12bfe3a1f9760e2bfa") else { fatalError("Missing URL")}
        
        let urlRequest = URLRequest(url: url)
        
        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        
        guard (response as? HTTPURLResponse)?.statusCode == 200 else {fatalError("Error fetching weather data")}
            
        let weatherData = try JSONDecoder().decode(WeatherData.self, from: data)

        return weatherData
    }
}

struct WeatherData: Codable {
    
    struct Coord: Codable {
        let lon: Double
        let lat: Double
    }

    struct Main: Codable {
        let temp: Double
        let feelsLike: Double
        let tempMin: Double
        let tempMax: Double
        let pressure: Int
        let humidity: Int
        let seaLevel: Int?  // Добавлено опциональное поле seaLevel

        enum CodingKeys: String, CodingKey {
            case temp, feelsLike = "feels_like", tempMin = "temp_min", tempMax = "temp_max"
            case pressure, humidity, seaLevel = "sea_level"  // Добавлено поле seaLevel в enum CodingKeys
        }
    }

    struct Clouds: Codable {
        let all: Int
    }

    let coord: Coord
    let main: Main
    let clouds: Clouds
}
