
const counter=document.querySelector(".counter");

async function addLike(id){

    fetch(`/like?id=${id}`, {
        method: 'POST',
    })
        .then(() => {
            counter.innerHTML = parseInt(counter.innerHTML) + 1;
        })
}

