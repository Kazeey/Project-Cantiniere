interface Order {
    userId: number,
    constraintId: number,
    quantity: [
        {
            quantity: number,
            mealId: number,
            menuId: number
        }
    ]
}

export { Order };