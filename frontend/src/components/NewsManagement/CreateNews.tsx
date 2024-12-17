import React, { useState } from "react";
import { Button, Card, Col, Container, Form, FormGroup, Row } from "react-bootstrap";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Controller, SubmitHandler, useForm } from "react-hook-form";
import CKEditor5_Custom from "../Utils/CKEditor5/CKEditor5_Custom";
import { Toast_Custom } from "../Utils/Toast_Custom";
import LoadingOverlay from "../Utils/LoadingOverlay/LoadingOverlay";
import BASE_URL from "../Api/BaseApi";

const CreateNewsSchema = z.object({
    title: z.string().min(1, "Title is required").max(100, "Title must be less than 100 characters"),
    content: z.string(),
}).passthrough()

type InputData = z.infer<typeof CreateNewsSchema>;

const CreateNews: React.FC = () => {
    document.title = "Create News";
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const nav = useNavigate();
    const {
        register, handleSubmit, reset, control,
        formState: { errors }
    } = useForm<InputData>({
        resolver: zodResolver(CreateNewsSchema),
    });


    const handleReset = () => {
        reset();
    };

    const onSubmit: SubmitHandler<InputData> = async (data) => {
        setIsLoading(true);
        try {
            const contentBlob = new Blob([data.content], { type: 'text/html' });
            const formData = new FormData();
            // Append the title as a regular field
            formData.append('title', data.title);
            // Append the content as a file
            formData.append('content', contentBlob, 'content.html');
            const token = localStorage.getItem("token");
            await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "multipart/form-data",
                },
                method: "POST",
                url: `${BASE_URL}/vaccination/news`,
                data: formData
            })
                .then(async (response) => {
                    if (response.status === 200) {
                        Toast_Custom({
                            type: "success",
                            message: "Create news successfully",
                        })
                        nav("/news/list")
                    }
                })
        } catch (error) {
            console.error("Error creating news:", error);
            if (axios.isAxiosError(error) && error.response) {
                if (error.response.data.message) {
                    Toast_Custom({
                        type: "error",
                        message: error.response.data.message
                    })
                }
            } else {
                Toast_Custom({
                    type: "error",
                    message: "An unknown error occurred"
                })
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Container>
            {isLoading ? <LoadingOverlay /> : null}
            <Row className="mt-4 mb-2">
                <h4 style={{ fontWeight: "bold" }}>CREATE NEWS</h4>
            </Row>
            <Card>
                <Card.Body>
                    <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                        <Row className="mb-4">
                            <FormGroup className="text-start">
                                <Form.Label htmlFor="title" className="fw-bold">
                                    Title<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <Form.Control
                                        {...register("title")}
                                        id="employeeName"
                                        type="text"
                                        isInvalid={!!errors?.title}
                                    />
                                </div>
                                {errors.title && (
                                    <p className="text-danger">{errors.title.message}</p>
                                )}
                            </FormGroup>
                        </Row>
                        <Row>
                            <FormGroup className="text-start">
                                <Form.Label htmlFor="content" className="fw-bold">
                                    Content<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <Controller
                                        control={control}
                                        name="content"
                                        render={({ field }) => (
                                            <CKEditor5_Custom
                                                onChange={(data) => field.onChange(data)}
                                                value={field.value}
                                            />
                                        )}
                                    />
                                </div>
                                {errors.content && (
                                    <p className="text-danger">{errors.content.message}</p>
                                )}
                            </FormGroup>
                        </Row>
                        <Row className="mb-4">
                            <Col className="d-flex justify-content-start">
                                <Button
                                    type="submit"
                                    variant="success"
                                    className="me-3 text-white rounded-0"
                                    style={{ width: "100px" }}
                                >
                                    Save
                                </Button>
                                <Button
                                    type="reset"
                                    variant="info"
                                    className="me-3 text-white rounded-0"
                                    style={{ width: "100px" }}
                                >
                                    Reset
                                </Button>
                                <Link
                                    to={"/news/list"}
                                    className={"btn btn-warning me-3 text-white rounded-0"}
                                    style={{ width: "100px" }}
                                >
                                    Cancel
                                </Link>
                            </Col>
                        </Row>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default CreateNews;