//
//  LoadingView.swift
//  Randomix
//
//  Created by Slava on 27/11/2023.
//

import SwiftUI

struct LoadingView: View {
    var body: some View {
        ZStack{
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle(tint: .black))
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .foregroundColor(.black)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color("Color1"))
    }
}

struct LoadingView_Previews: PreviewProvider {
    static var previews: some View {
        LoadingView()
    }
}
