import React, { useEffect, useState } from 'react';
import { Button, Card, Col, Container, Form, FormCheck, FormControl, FormGroup, FormText, Row } from "react-bootstrap";
import { SubmitHandler, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import axios from "axios";
import { Toast_Custom } from "../Utils/Toast_Custom";
import LoadingOverlay from "../Utils/LoadingOverlay/LoadingOverlay";
import { useLocation, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import BASE_URL from '../Api/BaseApi';

const RegisterInjectionSchema = z.object({
    date: z.string().date().min(1, "Date is required!"),
    contrain: z.array(z.string()).optional()
}).passthrough()

interface Response {
    username: string,
    vaccineName: string,
    from: string,
    end: string,
    place: string,
    times: number,
    indication: string,
    contrainditation: [string]
}

interface Token {
    username: string,
    scheduleId: number,
    iat: number,
    exp: number
}

type InputData = z.infer<typeof RegisterInjectionSchema>;

const RegisterInjection = () => {
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const tokenFromUrl = searchParams.get("token");

    const [isLoading, setIsLoading] = useState<boolean>(false);
    const nav = useNavigate();
    const [data, setData] = useState<Response>();
    const [isConfirmed, setIsConfirmed] = useState(false);
    const localToken = localStorage.getItem("token");
    document.title = "Register Inject";

    useEffect(() => {
        let urlUser = jwtDecode<Token>(tokenFromUrl!).username;
        if (!localToken) {
            Toast_Custom({
                type: "warning",
                message: "You are not logged in, please login first!",
            })
            nav("/login");
        } else if (urlUser === undefined) {
            nav("/403");
        } else {
            let localUser = jwtDecode(localToken).sub;
            if (urlUser !== localUser) {
                nav("/403");
            }
            fetchData().then();
        }
    }, [location]);

    const fetchData = async () => {
        try {
            const response = await axios.get<Response>(`${BASE_URL}/vaccination/injection/register/${tokenFromUrl}`, {});
            const responseData = response.data;
            console.log(responseData);
            setData(responseData);
        } catch (error) {
            console.error("Error fetching injection data:", error);
        }
    };

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm<InputData>({
        resolver: zodResolver(RegisterInjectionSchema),
    });

    const handleReset = () => {
        reset()
    };

    const onSubmit: SubmitHandler<InputData> = async (formData) => {
        setIsLoading(true);
        try {
            let urlUser = jwtDecode<Token>(tokenFromUrl!);
            let token = localStorage.getItem("token");
            const selectedContraindications = formData.contrain ? Object.values(formData.contrain).filter(Boolean) : [];
            const response = await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "POST",
                url: `${BASE_URL}/vaccination/injection/register`,
                data: {
                    date: formData.date,
                    username: urlUser.username,
                    injectionScheduleId: urlUser.scheduleId,
                    contrain: selectedContraindications
                },
            });
            if (response.status === 200) {
                Toast_Custom({
                    type: "success",
                    message: "Register injection successfully",
                })
            }
        } catch (error) {
            console.error("Error register injection:", error);
            if (axios.isAxiosError(error) && error.response) {
                const errorMessage = error.response.data.message || "An unknown error occurred";
                Toast_Custom({
                    type: "error",
                    message: errorMessage,
                })
            }
        } finally {
            setTimeout(() => {
                setIsLoading(false);
            }, 500);
        }
    };

    const handleConfirmChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setIsConfirmed(e.target.checked);
    };

    return (
        <Container className="mt-5">
            {isLoading ? <LoadingOverlay /> : null}
            <Card className="px-3 mb-5 border-2 ">
                <Card.Body>
                    <Row className="mt-2">
                        <h3 style={{ fontWeight: "bold" }}>REGISTER INJECTION</h3>
                    </Row>
                    <div className="text-center mb-5 fs-5 fst-italic">
                        {`Vaccine: ${data?.vaccineName} - Times: ${data?.times} - From: ${data?.from} - To: ${data?.end}`}
                    </div>
                    <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                        <Row className="mb-4">
                            <Col lg={5}>
                                <FormGroup className="text-start">
                                    <Form.Label className="fw-bold">
                                        Username:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl
                                            id="username"
                                            type="text"
                                            value={data?.username}
                                            className="border-1 border-secondary-subtle"
                                            disabled
                                        />
                                    </div>
                                </FormGroup>
                            </Col>
                            <Col lg={4}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="date" className="fw-bold">
                                        Date<span style={{ color: "red" }}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("date")}
                                            id="date"
                                            type="date"
                                            min={data?.from}
                                            max={data?.end}
                                            isInvalid={!!errors.date}
                                            className="border-1 border-secondary-subtle"
                                        />
                                    </div>
                                    {errors.date && (
                                        <p className="text-danger">{errors.date.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className={"mb-4"}>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label className="fw-bold">
                                        Place:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl
                                            type="text"
                                            value={data?.place}
                                            className="border-1 border-secondary-subtle"
                                            disabled
                                        />
                                    </div>
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className={"mb-4"}>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label className="fw-bold m-0">
                                        Indication:
                                    </Form.Label>
                                    <br />
                                    <FormText className="text-muted">(Please read the indication carefully. If you have
                                        any concerns, consult with your healthcare provider.)</FormText>
                                    <div className="d-flex align-items-center">
                                        <FormControl
                                            as="textarea"
                                            type="text"
                                            value={data?.indication}
                                            className="border-1 border-secondary-subtle"
                                            disabled
                                            style={{
                                                resize: 'none',
                                                overflow: 'auto',
                                                height: '150px',
                                                maxHeight: '1500px'
                                            }}
                                        />
                                    </div>
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className={"mb-4"}>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label className="fw-bold m-0">
                                        Contraindications<span style={{ color: "red" }}>(*):</span>
                                    </Form.Label>
                                    <br />
                                    <FormText className="text-muted">(Check any contraindications that apply to you. If
                                        you're unsure, please consult with your healthcare provider before
                                        proceeding.)</FormText>
                                    <div className="border border-1 border-secondary-subtle rounded p-3">
                                        {data?.contrainditation.map((item, index) => (
                                            <Row key={index} className="mb-2">
                                                <FormCheck
                                                    {...register(`contrain`)}
                                                    id={`contrain.${index}`}
                                                    type="checkbox"
                                                    value={item}
                                                    label={item}
                                                />
                                            </Row>
                                        ))}
                                    </div>
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-3">
                            <Col className="text-center">
                                <FormCheck
                                    type="checkbox"
                                    id="confirm-checkbox"
                                    label="I confirm that all the information provided is accurate and complete."
                                    checked={isConfirmed}
                                    onChange={handleConfirmChange}
                                    className="d-inline-block"

                                />
                            </Col>
                        </Row>
                        <Row>
                            <Col className="text-center">
                                <Button type="submit" className="me-2" disabled={!isConfirmed}>Register</Button>
                            </Col>
                        </Row>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default RegisterInjection;