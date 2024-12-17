import { Container } from 'react-bootstrap';
import './Error404.css'

function Error403() {
    return (
        <Container>
            <div>
                <div className='d-flex justify-content-center align-items-center text-center'>
                    <div className="err m-3">4</div>
                    <i className="far fa-solid fa-ban m-3" style={{ color: 'red' }}></i>
                    <div className="err m-3">3</div>
                </div>
                <h1>Access denied</h1>
                <div className="msg">You do not have permission to access this page. Please contact your Site Administrator(s) to request access<p>Let's go <a href="/">home</a> and try from there.</p></div>
            </div>
        </Container >
    )
}

export default Error403