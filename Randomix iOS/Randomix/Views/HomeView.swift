//
//  HomeView.swift
//  Randomix
//
//  Created by Slava on 27/11/2023.
//

import SwiftUI
import CoreLocationUI

struct HomeView: View {
    @EnvironmentObject var locationManager: LocationManager
    var body: some View {
        ZStack{
            VStack
            {
                VStack(spacing: 20)
                {
                    ZStack
                    {
                        Text("Welcome to the Randomix")
                            .bold()
                            .font(.title)
                            .foregroundColor(.black)
                    }
                }
                .padding()
                
                LocationButton(.shareCurrentLocation)
                {
                    locationManager.requestLocation()
                }
                .cornerRadius(30)
                .symbolVariant(.fill)
                .foregroundColor(.white)
                
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color("Color1"))
        
    }
}

struct HomeView_Previews: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
