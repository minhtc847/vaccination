import React, { useEffect, useState } from "react";
import { Button, Card, Col, Container, Form, FormCheck, FormControl, FormGroup, FormSelect, Row } from "react-bootstrap";
import axios from "axios";
import { Link, useNavigate, useParams } from "react-router-dom";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { SubmitHandler, useFieldArray, useForm } from "react-hook-form";
import { Toast_Custom } from "../../Utils/Toast_Custom";
import { UpdateVaccineSchema } from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof UpdateVaccineSchema>;

type vaccineType = {
    id: number;
    vaccineTypeName: string;
    status: boolean;
    description: string;
}


function UpdateVaccine() {
    // Set the document title
    document.title = "Update Vaccine";

    // State for storing vaccine types
    const [vaccineTypes, setVaccineTypes] = useState([]);

    // Get the id parameter from the URL
    const { id } = useParams<{ id: string }>();

    // State for managing rendering
    const [render, setRender] = useState(0);

    // State for managing loading state
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const nav = useNavigate();

    const blockInvalidChar = (e: React.KeyboardEvent<HTMLInputElement>) => ['e', 'E', '+', '-', '.'].includes(e.key) && e.preventDefault();

    // Form management using react-hook-form
    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
        watch,
        control,
        setValue
    } = useForm<InputData>({
        resolver: zodResolver(UpdateVaccineSchema),
    });

    // Field array management using react-hook-form
    const { fields, append, remove } = useFieldArray({
        control,
        name: "contraindication" as never,
    });

    useEffect(() => {
        const fetchVaccineTypes = async () => {
            const token = localStorage.getItem("token");
            const api = `${BASE_URL}/vaccination/vaccinetype?pageSize=1000`;
            const response = await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "GET",
                url: api,
            });
            const data = response.data;
            setVaccineTypes(data.content.filter((type: vaccineType) => type.status));
        };

        const fetchVaccineData = async () => {
            if (id) {
                try {
                    const token = localStorage.getItem("token");
                    const response = await axios.request({
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                        method: "GET",
                        url: `${BASE_URL}/vaccination/vaccine/${id}`,
                    });
                    const scheduleData = response.data;
                    reset(scheduleData);
                    setValue("contraindication", scheduleData.contraindication);
                    setValue("status", scheduleData.status);
                } catch (error) {
                    console.error("Error fetching schedule data:", error);
                }
            }
        };

        if (vaccineTypes.length === 0) {
            fetchVaccineTypes();
        }
        fetchVaccineData();
    }, [id, reset, render, vaccineTypes.length]);


    const handleReset = () => {
        setRender(render + 1);
    };


    const onSubmit: SubmitHandler<InputData> = async (data) => {
        setIsLoading(true);
        try {
            const token = localStorage.getItem("token");
            await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "PUT",
                url: `${BASE_URL}/vaccination/vaccine/${id}`,
                data: { ...data }
            })
                .then((response) => {
                    if (response.status === 200) {
                        Toast_Custom({
                            type: "success",
                            message: "Update vaccine successfully",
                        })
                        nav("/vaccine/list")
                    }
                })
        } catch (error) {
            console.error("Error updating vaccine:", error);
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
            setTimeout(() => {
                setIsLoading(false);
            }, 500)
        }
    }

    return (
        <Container>
            {isLoading ? <LoadingOverlay /> : null}
            <Row className="mt-4 mb-2">
                <h4 style={{ fontWeight: "bold" }}>UPDATE VACCINE</h4>
            </Row>
            <Card>
                <Card.Body>
                    <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                        <Row className="mb-4">
                            <Col md={8}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="id" className="fw-bold">
                                        Vaccine ID <span style={{ color: "red" }}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl
                                            type="text"
                                            value={id}
                                            readOnly
                                            disabled
                                        ></FormControl>
                                    </div>
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="status" className="fw-bold mb-3">
                                        Active<span style={{ color: "red" }}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormCheck
                                            id="status"
                                            type="checkbox"
                                            label="Active"
                                            defaultChecked={watch("status")}
                                            disabled
                                        >
                                        </FormCheck>
                                    </div>
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="vaccineName" className="fw-bold">
                                        Vaccine Name<span style={{ color: "red" }}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("vaccineName")}
                                            id="vaccineName"
                                            type="text"
                                            value={watch("vaccineName")}
                                            isInvalid={!!errors.vaccineName}
                                        />
                                    </div>
                                    {errors.vaccineName && (
                                        <p className="text-danger">{errors.vaccineName.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="vaccineTypeId" className="fw-bold">
                                        Vaccine Type<span style={{ color: "red" }}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormSelect
                                            {...register("vaccineTypeId", { valueAsNumber: true })}
                                            id="vaccineTypeId" isInvalid={!!errors.vaccineTypeId}>
                                            <option value={0}>Select vaccine type</option>
                                            {Array.isArray(vaccineTypes) && vaccineTypes.map((t: vaccineType) => (
                                                <option key={t.id} value={t.id}
                                                    defaultChecked={watch("vaccineTypeId") === t.id}>
                                                    {t.vaccineTypeName!}
                                                </option>
                                            ))}
                                        </FormSelect>
                                    </div>
                                    {errors.vaccineTypeId && (
                                        <p className="text-danger">{errors.vaccineTypeId.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="numberOfInjection" className="fw-bold">
                                        Number of Inject:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("numberOfInjection", { valueAsNumber: true })}
                                            id="numberOfInjection"
                                            type="number"
                                            min={1}
                                            onKeyDown={blockInvalidChar}
                                            value={watch("numberOfInjection")}
                                            isInvalid={!!errors.numberOfInjection}
                                        />
                                    </div>
                                    {errors.numberOfInjection && (
                                        <p className="text-danger">{errors.numberOfInjection.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="usage" className="fw-bold">
                                        Usage:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("usage")}
                                            id="usage"
                                            type="text"
                                            as="textarea"
                                            rows={4}
                                            value={watch("usage")}
                                            isInvalid={!!errors.usage}
                                        />
                                    </div>
                                    {errors.usage && (
                                        <p className="text-danger">{errors.usage.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="indication" className="fw-bold">
                                        Indication:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("indication")}
                                            id="indication"
                                            type="text"
                                            as="textarea"
                                            rows={4}
                                            value={watch("indication")}
                                            isInvalid={!!errors.indication}
                                        />
                                    </div>
                                    {errors.indication && (
                                        <p className="text-danger">{errors.indication.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <p className="fw-bold text-start">
                                    Contraindication:
                                </p>
                                {fields.map((field, index) => (
                                    <Row key={field.id} className="mb-3 align-items-start">
                                        <Col md={"auto"} className="text-start pe-0 mt-1">
                                            <Form.Label htmlFor={`contraindication.${index}`}>
                                                Contraindication #{index + 1}:
                                            </Form.Label>
                                        </Col>
                                        <Col md={4} className="text-start">
                                            <div className="d-flex align-items-center ">
                                                <FormControl
                                                    id={`contraindication.${index}`}
                                                    {...register(`contraindication.${index}`)}
                                                    placeholder="Enter contraindication..."
                                                    isInvalid={!!errors.contraindication?.[index]}
                                                />
                                                <Button onClick={() => remove(index)}
                                                    className="ms-2 btn-close">
                                                </Button>
                                            </div>
                                            {errors.contraindication && errors.contraindication[index] && (
                                                <Form.Control.Feedback type="invalid" className="d-block">
                                                    {errors?.contraindication[index]?.message}
                                                </Form.Control.Feedback>
                                            )}
                                        </Col>
                                    </Row>
                                ))}
                                <Row>
                                    <Col className={"text-start"}>
                                        <Button type="button" variant="light" onClick={() => append('')}
                                            className="border border-2 rounded-pill text-secondary">
                                            + Add Contraindication
                                        </Button>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="timeBeginNextInjection" className="fw-bold">
                                        Time of beginning next injection:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("timeBeginNextInjection", { valueAsNumber: true })}
                                            id="timeBeginNextInjection"
                                            type="number"
                                            min={1}
                                            onKeyDown={blockInvalidChar}
                                            value={watch("timeBeginNextInjection")}
                                            isInvalid={!!errors.timeBeginNextInjection}
                                        />
                                    </div>
                                    {errors.timeBeginNextInjection && (
                                        <p className="text-danger">{errors.timeBeginNextInjection.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="totalInject" className="fw-bold">
                                        Total Inject:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("totalInject", { valueAsNumber: true })}
                                            id="totalInject"
                                            type="number"
                                            min={1}
                                            onKeyDown={blockInvalidChar}
                                            value={watch("totalInject")}
                                            isInvalid={!!errors.totalInject}
                                        />
                                    </div>
                                    {errors.totalInject && (
                                        <p className="text-danger">{errors.totalInject.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="origin" className="fw-bold">
                                        Origin:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("origin")}
                                            id="origin"
                                            type="text"
                                            value={watch("origin")}
                                            isInvalid={!!errors.origin}
                                        />
                                    </div>
                                    {errors.origin && (
                                        <p className="text-danger">{errors.origin.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
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
                                    to={"/vaccine/list"}
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
    )
}

export default UpdateVaccine