import { Container } from 'react-bootstrap';
import './Error404.css'
function Error404() {
    return (
        <Container>
            <div>
                <div className='d-flex justify-content-center align-items-center text-center'>
                    <div className="err m-3">4</div>
                    <i className="far fa-question-circle fa-spin m-3"></i>
                    <div className="err m-3">4</div>
                </div>
                <div className="msg">Maybe this page moved? Got deleted? Is hiding out in quarantine? Never existed in the first place?<p>Let's go <a href="/">home</a> and try from there.</p></div>
            </div>
        </Container >
    )
}

export default Error404