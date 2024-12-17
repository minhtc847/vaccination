import axios from 'axios'
import React, { useEffect, useState } from 'react'
import { Container } from 'react-bootstrap'
import { useNavigate, useParams, Link } from 'react-router-dom'
import { News } from '../../Interface/BusinessObjectInterface'
import { showErrorAlert } from '../../Utils/ErrorAlert'
import './NewsDetail.css'
import BASE_URL from '../../Api/BaseApi'

const NewsDetail: React.FC = () => {

    const [news, setNews] = useState<News | null>(null);
    const [htmlContent, setHtmlContent] = useState<string>("");
    const { id } = useParams();
    const token = localStorage.getItem("token");
    const nav = useNavigate();
    if (news) {
        document.title = news.title
    }

    useEffect(() => {
        axios.get(`${BASE_URL}/vaccination/news/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        })
            .then(response => {
                const result = response.data;
                setNews(result);
            })
            .catch(error => {
                if (error.response) {
                    showErrorAlert(() => {
                        nav("/");
                    }, error.response.data.message)
                }
            })
    }, [id, token, nav])

    useEffect(() => {
        const fetchHTMLContent = async () => {
            if (news && news.content) {
                try {
                    const htmlFile = await fetch(news.content);
                    const htmlText = await htmlFile.text();
                    setHtmlContent(htmlText);
                } catch (error) {
                    console.error('Error fetching the HTML content:', error);
                }
            }
        };

        fetchHTMLContent();
    }, [news])

    useEffect(() => {
        const handleScroll = () => {
            const container = document.querySelector('.news-container');
            const btn = document.getElementById('button');
            if (btn && container && container.scrollTop > 300) {
                btn.classList.add('show');
            } else {
                btn?.classList.remove('show');
            }
        };

        const container = document.querySelector('.news-container');
        container?.addEventListener('scroll', handleScroll);
        return () => {
            container?.removeEventListener('scroll', handleScroll);
        };
    }, []);

    const scrollToTop = () => {
        const container = document.querySelector('.news-container');
        if (container) {
            container.scrollTo({ top: 0, behavior: 'smooth' });
        }
    };

    return (
        <Container className='pt-4'>
            <Link to={`/news/list`} className={'custom-link'}>Back to list</Link>
            <Container className='pt-4' style={{ backgroundColor: "white" }}>
                <h1 className='news-title'>{news?.title}</h1>
                <div className='news-container'>
                    <a id="button" onClick={scrollToTop}></a>
                    <div dangerouslySetInnerHTML={{ __html: `${htmlContent}` }} className='news-content'>
                    </div>
                </div>
            </Container>
        </Container>
    )
}

export default NewsDetail