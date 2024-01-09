//
//  RandomixManager.swift
//  Randomix
//
//  Created by Slava on 19/12/2023.
//

import Foundation
import GameplayKit

class Randomix
{
    private var number: WeatherData
    
    init(_ responseBody: WeatherData)
    {
        number = responseBody
    }
    
    func shuffleBits(from mask: UInt64) -> [Bool] {
        var bitsArray: [Bool] = []
        
        // Convert the bits of the mask to an array of Bool
        for i in 0..<64 {
            let bit = mask & (1 << i) != 0
            bitsArray.append(bit)
        }
        
        // Shuffle the bits array
        bitsArray.shuffle()
        
        return bitsArray
    }
    
    func createNumberFromMask(range: ClosedRange<Int>) -> Int {
        let mask = createMaskFromNumber()
        guard range.lowerBound <= range.upperBound else {
            fatalError("Lower bound of the range must be less than or equal to the upper bound.")
        }
        
        // Convert the bits of the mask to a shuffled array of Bool
        let shuffledBits = shuffleBits(from: mask)
        
        // Determine the number of bits required to represent the number in the range
        let requiredBitCount = Int(ceil(log2(Double(abs(range.upperBound - range.lowerBound) + 1))))
        
        // Initialize the result with the shuffled bits
        var result: UInt64 = 0
        for (index, bit) in shuffledBits.prefix(requiredBitCount).enumerated() {
            if bit {
                result |= (1 << index)
            }
        }
        
        // Ensure that the result is within the specified range by masking out excess bits
        result &= (1 << requiredBitCount) - 1
        
        // Convert the result to an integer and adjust to the specified range
        let finalResult = max(range.lowerBound, min(range.upperBound, Int(result)))
        
        return finalResult
    }
    
    
    
    func createMaskFromNumber() -> UInt64 {
        // Используем битовые операции для создания более разнообразной маски
        var resultMask = (UInt64(number.main.tempMax) << 10) +
        (UInt64(number.main.tempMin) << 20) +
        (UInt64(number.main.temp) << 30) +
        (UInt64(number.main.humidity) << 40) +
        (UInt64(number.main.pressure) << 50)
        
        // Переставляем биты для большей случайности
        resultMask = (resultMask << 7) | (resultMask >> 57)
        
        return resultMask
    }
}

