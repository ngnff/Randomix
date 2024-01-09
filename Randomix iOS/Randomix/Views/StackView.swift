//
//  StackView.swift
//  Randomix
//
//  Created by Slava on 08/01/2024.
//

import SwiftUI

struct StackView: View {
    
    let gradient = Gradient(colors: [.gray, .white])
    
    @State private var resultNumber: Int = -1
    
    @State private var minNumber: Int?
    
    @State private var maxNumber: Int?
    
    @State private var List: Array<Int> = []
    
    var weather: WeatherData
    
    private var randomix: Randomix {
        return Randomix(weather)
    }
    
    @State private var range = 0...0
    
    var body: some View {
        ZStack
        {
            VStack
            {
                Text("Stack Random")
                    .font(.title)
                    .bold()
                    .foregroundColor(.black)
                
                Spacer()
                
                HStack
                {
                    
                    VStack{
                        
                        Text("min")
                            .bold()
                            .foregroundColor(.black)
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(Color.black, lineWidth: 1)
                                    .frame(width: 100, height: 30)
                            )
                            .padding()
                        
                        
                        TextField("" , value: $minNumber, format: .number)
                            .keyboardType(.numberPad)
                            .background(RoundedRectangle(cornerRadius: 20).fill(Color("Color2")).frame(height: 50))
                            .foregroundColor(.black)
                            .padding(.horizontal)
                            .multilineTextAlignment(.center)
                        
                    }
                    
                    Spacer()
                    
                    VStack{
                        Text("max")
                            .bold()
                            .foregroundColor(.black)
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(Color.black, lineWidth: 1)
                                    .frame(width: 100, height: 30)
                            )
                            .padding()
                        
                        TextField("" , value: $maxNumber, format: .number)
                            .keyboardType(.numberPad)
                            .background(RoundedRectangle(cornerRadius: 20).fill(Color("Color2")).frame(height: 50))
                            .foregroundColor(.black)
                            .padding(.horizontal)
                            .multilineTextAlignment(.center)
                    }
                }
                
                Spacer()
                
                Button(action: {
                    UIApplication.shared.endEditing()
                    if (minNumber != nil && maxNumber != nil) {
                        range = minNumber!...maxNumber!
                        resultNumber = randomix.createNumberFromMask(range: range)
                        List.append(resultNumber)
                    }
                })
                {
                    ZStack{
                        Circle()
                            .foregroundColor(.white)
                            .frame(width: 150, height: 150)
                        
                        Circle()
                            .foregroundColor(.white)
                            .frame(width: 130, height: 130)
                            .shadow(radius: 8)
                        
                        Triangle()
                            .foregroundColor(Color("Color2"))
                            .frame(width: 35, height: 35)
                            .shadow(radius: 8)
                    }
                }
                
                Spacer()
                
                ScrollView
                {
                    LazyVGrid(columns: [GridItem(.flexible(), spacing: 6, alignment: nil), GridItem(.flexible(), spacing: 6, alignment: nil)])
                    {
                        ForEach(List, id: \.self) { List in
                            ZStack
                            {
                                Rectangle()
                                    .foregroundColor(Color("Color2"))
                                    .frame(height: 100)
                                    .cornerRadius(20)
                                
                                Text("\(List)")
                            }
                        }
                        
                    }
                }.frame(width: 300, height: 300)
                    .padding(.horizontal)
            }.padding(.vertical)
        }
        .background(Color("Color1"))
    }
}


struct StackView_Previews: PreviewProvider {
    static var previews: some View {
        StackView(weather: PreviewWeather).preferredColorScheme(.light)
    }
}
