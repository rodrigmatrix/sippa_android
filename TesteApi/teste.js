async function getMovies() {
    const url = "https://sistemas.quixada.ufc.br/apps/sippa"
    const options = {
        method: 'GET',
        headers: new Headers({
        'Content-Type': 'application/x-www-form-urlencoded'
        })
    }
    fetch(url,options)
    .then(response => console.log(response.text()))
}