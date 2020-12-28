interface Menu {
    id?: number,
    description: String,
    label: String
    image: {
        imagePath : String,
        image64: String
    }
    priceDF: number,
    availableForWeeks: number[],
    mealIds: number[]
}

export { Menu };