import axios from 'axios';
import React, { useState, useEffect } from 'react'
import { Card, Col, Container, Form, Row, InputGroup } from 'react-bootstrap';
import { useLocation, useNavigate, Navigate } from 'react-router-dom';
import BASE_URL from './Api/BaseApi';

function SetPassword() {
    const [newPassword, setNewPassword] = useState<string>("");
    const [confirmPassword, setConfirmPassword] = useState<string>("");
    const [message, setMessage] = useState<string>("");
    const [showNewPassword, setShowNewPassword] = useState<boolean>(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState<boolean>(false);
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const token = searchParams.get('token');
    const nav = useNavigate();
    const [isValidToken, setIsValidToken] = useState(false);
    document.title = "Change Password";
    useEffect(() => {
        if (token) {
            axios
                .get(`${BASE_URL}/vaccination/employee/check-token-isvalid`, {
                    params: { token }
                })
                .then(response => {
                    setIsValidToken(response.data)
                })
        }
    }, [token]);

    if (isValidToken) {
        return <Navigate to="/404" />;
    }

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d!@#$%^&*]{8,}$/;
        if (newPassword !== confirmPassword) {
            setMessage("New password and confirm password do not match.");
            return;
        }
        if (!regex.test(newPassword)) {
            setMessage("The new password must be at least 8 characters long, contain an uppercase letter, an lowercase letter, a number.");
            return;
        }
        else {
            axios
                .put(`${BASE_URL}/vaccination/employee/set-password`, null, {
                    headers: {
                        'newPassword': newPassword
                    }, params: {
                        'token': token
                    }
                })
                .then((response) => {
                    setMessage(response.data);
                    // Set success message or display other information to the user
                    setTimeout(() => {
                        nav("/");
                    }, 3000);
                })
                .catch((error) => {
                    setMessage(error.response.data.message);
                })
        }
    }
    function showPassword(input: string): void {
        const x = document.getElementById(input) as HTMLInputElement;
        if (x.type === "password") {
            x.type = "text";
        } else {
            x.type = "password";
        }
    }

    if (!token) {
        return <Navigate to="/404" />;
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
                                            <Form.Label className='pt-1'>New password:</Form.Label>
                                        </Col>
                                        <Col sm={8}>
                                            <InputGroup>
                                                <Form.Control type='password' name="newPassword" placeholder='New Password' value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required>
                                                </Form.Control>
                                                <InputGroup.Text>
                                                    <button type="button" onClick={() => {
                                                        showPassword("newPassword");
                                                        setShowNewPassword(!showNewPassword);
                                                    }}>
                                                        <i className={showNewPassword ? "fas fa-eye" : "fas fa-eye-slash"}></i>
                                                    </button>
                                                </InputGroup.Text>
                                            </InputGroup>
                                        </Col>
                                    </Form.Group>
                                    <Form.Group as={Row} className="mb-4" controlId='confirmPassword'>
                                        <Col sm={4}>
                                            <Form.Label className='pt-1'>Confirm password:</Form.Label>
                                        </Col>
                                        <Col sm={8}>
                                            <InputGroup>
                                                <Form.Control type='password' name="confirmPassword" placeholder='New Password' value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required>
                                                </Form.Control>
                                                <InputGroup.Text>
                                                    <button type="button" onClick={() => {
                                                        showPassword("confirmPassword");
                                                        setShowConfirmPassword(!showConfirmPassword);
                                                    }}>
                                                        <i className={showConfirmPassword ? "fas fa-eye" : "fas fa-eye-slash"}></i>
                                                    </button>
                                                </InputGroup.Text>
                                            </InputGroup>
                                        </Col>
                                    </Form.Group>
                                    <button type="submit" className="btn m-4 w-auto" style={{ borderColor: "black", color: "black" }}>Change password</button>
                                </Form>
                                {message && <div className="mt-3">{message}</div>}
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </Container>
    )
}

export default SetPassword