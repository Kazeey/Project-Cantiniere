function getItem(item)
{
  return(localStorage.getItem(item))
}

export function verification()
{
    let timeNow = String(Date.now()); // Récupère le timestamp pour comparer par rapport au TTL des variables de session

    if(getItem("connected") == "true")
    {
      if(parseInt(timeNow) > parseInt(localStorage.getItem("timeDestruction")))
      {
        //console.log("TTL dépassé"); // Pour le débuggage
        localStorage.setItem("connected", "false");
        return false;
      }
      else
      {
        //console.log("TTL pas encore atteint"); // Pour le débuggage
        return true;
      }
    }
    else
    {
      return false;
    }
}