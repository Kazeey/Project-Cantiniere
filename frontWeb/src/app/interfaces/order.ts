interface Orders{
    id?: number,
    creationDate: String,
    creationTime: {
      hour: number,
      minute: number,
      second: number,
      nano: number
    },
    status: number,
    user: {
      id?: number,
      address: String,
      wallet: number,
      postalCode: number,
      registrationDate: String,
      email: String,
      isLunchLady: Boolean,
      name: String,
      firstname: String,
      phone: number,
      town: String,
      sex: number,
      status: number,
      imageId: number
    },
    quantity: [
      {
        id?: number,
        quantity: number,
        meal: {
          id: number,
          description: string,
          label: string,
          status: number,
          imageId: number,
          priceDF: number,
          availableForWeeks: [
            number
          ],
          ingredients: [
            {
              id?: number,
              description: string,
              label: string,
              status: number,
              imageId: number
            }
          ]
        },
        menu: {
          id?: number,
          description: string,
          label: string,
          status: number,
          imageId: number,
          priceDF: number,
          availableForWeeks: [
            number
          ],
          meals: [
            {
              id?: number,
              description: string,
              label: string,
              status: number,
              imageId: number,
              priceDF: number,
              availableForWeeks: [
                number
              ],
              ingredients: [
                {
                  id?: number,
                  description: string,
                  label: string,
                  status: number,
                  imageId: number
                }
              ]
            }
          ]
        }
      }
    ]
}

export { Orders };
