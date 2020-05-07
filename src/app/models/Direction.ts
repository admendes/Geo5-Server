export class Direction{
    id:number
    title:String
    description:String
    travelMode:String
    origin: {lat: number, lng: number}
    destination:{lat: number,lng: number}
    waypoints:[
      {location: {lat: number,lng: number}},
      {location: {lat: number,lng: number}}
    ]
    visible:boolean
}