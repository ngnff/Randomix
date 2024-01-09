//
//  RandomView.swift
//  Randomix
//
//  Created by Slava on 20/12/2023.
//

import SwiftUI

struct RandomView: View {
    
    let gradient = Gradient(colors: [.gray, .white])
    
    @State private var resultNumber: Int = -1
    
    @State private var minNumber: Int?
    
    @State private var maxNumber: Int?
    
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
                Text("Classic Random")
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
                    }
                }
                Spacer()
                
                ZStack{
                    Rectangle()
                        .foregroundColor(Color("Color2"))
                        .frame(width: .infinity, height: 150)
                        .cornerRadius(20)
                        .padding(.horizontal)
                    
                    if (resultNumber >= 0){
                        Text("\(resultNumber)")
                            .font(.largeTitle)
                            .fontWeight(.heavy)
                            .foregroundColor(.black)
                            .bold()
                    }
                    
                }
                
                Spacer()
            }.padding(.vertical)
        }
        .background(Color("Color1"))
    }
}

struct RandomView_Previews: PreviewProvider {
    static var previews: some View {
        return RandomView(weather: PreviewWeather).preferredColorScheme(.light)
    }
}

extension UIApplication {
    func endEditing() {
        sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}

struct Triangle: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        
        // Start from the bottom left
        path.move(to: CGPoint(x: rect.minX, y: rect.maxY))
        // Add line to the top middle
        path.addLine(to: CGPoint(x: rect.midX, y: rect.minY))
        // Add line to the bottom right
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY))
        // Close the path to create the third side of the triangle
        path.closeSubpath()
        
        return path
    }
}
