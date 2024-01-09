//
//  ContentView.swift
//  Randomix
//
//  Created by Slava on 27/11/2023.
//

import SwiftUI

struct ContentView: View {
    @StateObject var locationManager = LocationManager()
    var weatherManager = WeatherManager()
    @State var weather: WeatherData?
    
    @State var flag = false
    
    var body: some View {
        VStack {
            
            if let location = locationManager.location {
                if let weather = weather {
                    PageView(weather: weather)
                }
                else
                {
                    LoadingView()
                        .task {
                            do
                            {
                                weather = try await weatherManager.getCurrentWeather(latitude: location.latitude, longitude: location.longitude)
                            }
                            catch
                            {
                                print("Error getting weather: \(error)")
                            }
                        }
                }
            }
            else
            {
                if locationManager.isLoading
                {
                    LoadingView()
                }
                else
                {
                    if !flag
                    {
                        VStack
                        {
                            HomeView().environmentObject(locationManager)
                            
                            Button(action: {
                                flag.toggle()
                            })
                            {
                                Text("Simplified version")
                                    .foregroundColor(.gray)
                            }
                        }
                        
                    }
                    else
                    {
                        PageView(weather: PreviewWeather)
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color("Color1"))
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
