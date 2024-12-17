import React, { useEffect, useState } from "react";
import { Button, Form, Modal, Row } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircle, faUserCircle } from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { z } from "zod";
import { SubmitHandler, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Bounce, toast } from "react-toastify";
import BASE_URL from "./Api/BaseApi";

const inputSchema = z.object({
    username: z.string().min(1, "Username must be not empty!").min(8, "Username must at least 8 character").max(255),
    password: z.string().min(1, "Password must be not empty!").min(8, "Password must at least 8 character").max(255),
});

type InputData = z.infer<typeof inputSchema>;

const Login = () => {
    document.title = "Login";
    const [show, setShow] = useState<boolean>(false);
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        if (location.pathname === "/login") {
            setShow(true);
        } else {
            setShow(false);
        }
    }, [location]);

    const handleShow = () => {
        setShow(!show);
        if (show) {
            navigate("/");
        } else {
            navigate("/login");
        }
    };

    const {
        register, handleSubmit, reset,
        formState: { errors }
    } = useForm<InputData>({
        resolver: zodResolver(inputSchema),
    });

    const onSubmit: SubmitHandler<InputData> = async (data) => {
        try {
            const parsedData = inputSchema.parse(data);
            await axios.request({
                method: "POST",
                url: `${BASE_URL}/vaccination/employee/login`,
                data: parsedData,
            })
                .then(async (response) => {
                    if (response.status === 200) {
                        const token = response.data.token;
                        localStorage.setItem("token", token);
                        const userInfo = {
                            username: jwtDecode(token).sub,
                            email: jwtDecode(token).iss,
                            role: jwtDecode(token).aud,
                            image: jwtDecode(token).jti,
                        };
                        localStorage.setItem("userInfo", JSON.stringify(userInfo));
                        toast.success("Login successful", {
                            position: "top-right",
                            autoClose: 5000,
                            hideProgressBar: false,
                            closeOnClick: true,
                            pauseOnHover: true,
                            draggable: true,
                            progress: undefined,
                            theme: "light",
                            transition: Bounce,
                        });
                        navigate(-1);
                    }
                })
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                if (error.response.data.message) {
                    toast.error(error.response.data.message, {
                        position: "top-right",
                        autoClose: 5000,
                        hideProgressBar: false,
                        closeOnClick: true,
                        pauseOnHover: true,
                        draggable: true,
                        progress: undefined,
                        theme: "light",
                        transition: Bounce,
                    });
                }
            } else {
                toast.error("An unknown error occurred", {
                    position: "top-right",
                    autoClose: 5000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                    progress: undefined,
                    theme: "light",
                    transition: Bounce,
                });
            }
        }
    };

    return (
        <>
            <Modal show={show} centered>
                <FontAwesomeIcon
                    className="text-white text-center"
                    icon={faCircle}
                    style={{
                        fontSize: "99px",
                        left: "50%",
                        position: "absolute",
                        transform: "translate(-50%, -60%)",
                    }}
                />
                <FontAwesomeIcon
                    className="text-info text-center"
                    icon={faUserCircle}
                    style={{
                        fontSize: "100px",
                        left: "50%",
                        position: "absolute",
                        transform: "translate(-50%, -60%)",
                    }}
                />
                <Button
                    variant="link"
                    size="sm"
                    className="btn-close align-self-end m-3"
                    onClick={handleShow}
                ></Button>
                <Modal.Body>
                    <h2
                        style={{ fontWeight: "inherit" }}
                        className="text-center text-secondary text-opacity-50"
                    >
                        Member Login
                    </h2>
                    <Form className="px-5 mt-3" onSubmit={handleSubmit(onSubmit)}>
                        <Row className="mb-3">
                            <Form.Control
                                {...register("username")}
                                type="text"
                                placeholder="Username"
                                isInvalid={!!errors.username}
                            />
                            {errors.username && (
                                <p className="text-danger">{errors.username.message}</p>
                            )}
                        </Row>
                        <Row className="mb-3">
                            <Form.Control
                                {...register("password")}
                                type="password"
                                placeholder="Password"
                                isInvalid={!!errors.password}
                            />
                            {errors.password && (
                                <p className="text-danger">{errors.password.message}</p>
                            )}
                        </Row>
                        <Row className="mb-3">
                            <Button variant="info" className="text-white" type="submit">
                                Sign in
                            </Button>
                        </Row>
                    </Form>
                </Modal.Body>
                <Modal.Footer className="justify-content-center bg-secondary-subtle">
                    <Form.Text className="fs-6">
                        <Link to={'/forgot-password'} style={{ textDecoration: 'none' }} className="text-secondary"
                            onClick={() => setShow(false)}>Forgot Password?</Link>
                    </Form.Text>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default Login;
