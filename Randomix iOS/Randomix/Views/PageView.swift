//
//  PageView.swift
//  Randomix
//
//  Created by Slava on 09/01/2024.
//

import SwiftUI

struct PageView: View {
    
    var weather: WeatherData
    
    var body: some View {
        TabView
        {
            RandomView(weather: weather)
                                        
            StackView(weather: weather)
        }
        .tabViewStyle(.page(indexDisplayMode: .always))
                .indexViewStyle(PageIndexViewStyle(backgroundDisplayMode: .always))
    }
}

struct PageView_Previews: PreviewProvider {
    static var previews: some View {
        PageView(weather: PreviewWeather)
    }
}
