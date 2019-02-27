async function getMovies() {
    const url = "https://cors.io/?https://sistemas.quixada.ufc.br/apps/sippa/index.jsp"
    fetch(url, {
      method: 'GET',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      credentials: 'same-origin',
    }).then(res => {
      console.log(res.headers.get('set-cookie')); // undefined
      console.log(document.cookie); // nope
      return res.json();
    }).then(json => {
      if (json.success) {
        this.setState({ error: '' });
        this.context.router.push(json.redirect);
      }
      else {
        this.setState({ error: json.error });
      }
    });
}