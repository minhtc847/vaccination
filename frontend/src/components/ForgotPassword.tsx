import React, { useState } from 'react'
import axios from 'axios'
import { Col, Container, Row, Card, Form } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import BASE_URL from './Api/BaseApi';

const ForgotPassword: React.FC = () => {
    const [email, setEmail] = useState<string>("");
    const [message, setMessage] = useState<string>("");
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const nav = useNavigate();
    document.title = "Forgot Password";

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setIsLoading(true);

        axios
            .put(`${BASE_URL}/vaccination/employee/forgot-password`, null, {
                headers: {
                    'email': email
                }
            })
            .then((response) => {
                setMessage(response.data);
            })
            .catch((error) => {
                setMessage(error.response.data.message);
            })
            .finally(() => {
                setIsLoading(false); // Tắt hiệu ứng loading khi xong
            });

    }


    return (
        <Container className='pt-4'>
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>Change Password</h5>
            <Container className='d-flex flex-column w-50'>
                <Row className="justify-content-center align-items-center">
                    <Col>
                        <Card>
                            <Card.Body>
                                <Form onSubmit={handleSubmit} className='d-inline-flex"'>
                                    <Form.Group as={Row} className="mb-4" controlId='newPassword'>
                                        <Col sm={4}>
                                            <Form.Label className='pt-2'>Email</Form.Label>
                                        </Col>
                                        <Col sm={8}>
                                            <Form.Control type='email' name="email" placeholder='Input Email' value={email} onChange={(e) => setEmail(e.target.value)}
                                                required>
                                            </Form.Control>
                                        </Col>
                                    </Form.Group>
                                    <button type="submit" className="btn text-center mt-1" style={{ borderColor: "black", color: "black", marginRight: '5px' }}>
                                        {isLoading ? (
                                            <>
                                                <span className="fa fa-spinner fa-spin"></span> It will take few second...
                                            </>
                                        ) : 'Reset password'}</button>
                                    <button className="btn text-center mt-1" style={{ borderColor: "black", color: "black" }} onClick={() => nav("/")}>Cancel</button>
                                </Form>
                                {message && <div className="text-center mt-3">{message}</div>}
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </Container>
    )
}

export default ForgotPassword